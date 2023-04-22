package com.resdii.vars.services.postService;

import com.google.common.hash.Hashing;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.helper.LinksHelper;
import com.resdii.vars.helper.RedisHelper;
import com.resdii.vars.services.scraperWebservice.scraperServiceFactory.ScraperServiceFactory;
import com.resdii.vars.services.WebBaseScraperImpl;
import com.resdii.vars.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.resdii.vars.constants.GlobalConstant.failedLoadPage;
import static com.resdii.vars.constants.GlobalConstant.hitCurrentPlan;
import static com.resdii.vars.utils.CommonUtils.findLinks;

public class BDSWebSubPageScraperImpl extends WebBaseScraperImpl {

    protected String baseUrl;
    protected WebScraperBDSDetailImpl webScraper;
    protected ScraperServiceFactory scraperServiceFactory;
    protected LinksHelper linksHelper;
    protected RedisHelper redisHelper;

    public void getLinksByPostType(Integer command, String baseUrl, String prefix){
        int listKeyLength=apiKeyHelper.getApi_key_for_load_paging().length;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        Integer numPage= linksHelper.getMaxPageIndex(baseUrl, command);
        for (int i = 1; i <= numPage; i++) {
            int currentKeyIndex=i % listKeyLength;
            String apiKey=apiKeyHelper.getApi_key_for_load_paging()[currentKeyIndex];
            Integer postPageIndex=i;
            executor.submit(()->{
                System.out.println(postPageIndex);
                String urlWithPageIndex= linksHelper.getLinksPageIndex( baseUrl, command, postPageIndex);
                String pageHashValue= Hashing.sha256().hashString(urlWithPageIndex, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:*:"+command+":"+pageHashValue).size()==0){
                    String htmlPage= loadPage(urlWithPageIndex, apiKey).html();
                    savedDetailLink(findLinks(htmlPage, GlobalConstant.baseUrlToClassNameForGetLinks.get(baseUrl)), command, baseUrl, prefix);
                    if(htmlPage.contains(failedLoadPage) || htmlPage.contains(hitCurrentPlan)){
                        redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":"+pageHashValue, command+"_"+urlWithPageIndex);
                    }else{
                        redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:SUCCESS:"+command+":"+pageHashValue,command+"_"+urlWithPageIndex );
                    }

                }
            });
        }
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
        f.thenApply(integer -> {
            getLinksByPostTypeFailed(command, baseUrl, prefix);
            System.out.println("Done Get Link");
            return null;
        });
    }
    public void getLinksByPostTypeFailed(Integer command, String baseUrl,String prefix) {
        ExecutorService executor = Executors.newFixedThreadPool(apiKeyHelper.getApi_key_for_load_paging().length*5);
        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":*");
        List<String> values = ops.multiGet(keys);
        for (int i = 0; i < values.size(); i++) {
            String api_key=apiKeyHelper.getApi_key_for_load_paging()[i % apiKeyHelper.getApi_key_for_load_paging().length];
            String ele=values.get(i);
            executor.submit(()->{
                String[] pagingURL=ele.split("_");
                String htmlPage= loadPage(pagingURL[1], api_key).html();
                String pageHashValue= Hashing.sha256().hashString(pagingURL[1], StandardCharsets.UTF_8).toString();
                if(!htmlPage.contains(failedLoadPage) && !htmlPage.contains(hitCurrentPlan)){
                    redisHelper.template.delete("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":"+pageHashValue);
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:SUCCESS:"+command+":"+pageHashValue,ele );
                    savedDetailLink(findLinks(htmlPage, GlobalConstant.baseUrlToClassNameForGetLinks.get(baseUrl)), command, baseUrl, prefix);
                }
            });
        }
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
        f.thenApply(integer -> {
            Set<String> failedKeys = redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":*");
            List<String> failedValue = ops.multiGet(failedKeys);
            if(failedValue.size()!=0){getLinksByPostTypeFailed(command, baseUrl, prefix);}
            else{
                executor.shutdown();
                System.out.println("Done");
            }
            return null;
        });
    }
    public int savedDetailLink(List<String> listLinks, Integer command, String baseUrl, String prefix){
        AtomicInteger count= new AtomicInteger();
        listLinks.forEach(detailUrl->{
            String url= baseUrl +detailUrl;
            String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
            if(redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:*:"+command+":"+hashValue).size()==0){
                System.out.println(url);
                count.set(count.get() + 1);
                redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PENDING:"+command+":"+hashValue, command+"_"+url);
            }
        });
        return count.get();
    }
    public void getDetailPage(int numOfItems, int numOfThread, String prefix){
        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:PENDING:[0,1]:*");
        List<String> values = ops.multiGet(keys);
        for (int i = 0; i < numOfItems; i++) {
            String api_key=apiKeyHelper.getApi_keys()[i % apiKeyHelper.getApi_keys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.substring(2);
                System.out.println(url);
                String command=ele.substring(0,1);
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:FAILED:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:TRY:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue, command+"_"+url);
                    redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:PENDING:"+command+":"+hashValue);
                    PostDocument postDocument=new PostDocument();
                    PostStatus postStatus=webScraper.scrape(url, Integer.parseInt(command), api_key, postDocument);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(Integer.parseInt(command), url, prefix , hashValue, postStatus, false);
                }
            });
        }
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
        f.thenApply(integer -> {
            executor.shutdown();
            System.out.println("Done");
            return null;
        });
    }
    public void runFailedCase(int numOfItems, int numOfThread){
        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:DETAIL:FAILED:*");
        List<String> values = ops.multiGet(keys);
        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfItems; i++) {
            String api_keys=apiKeyHelper.getApi_keys()[i % apiKeyHelper.getApi_keys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String[] url=ele.split("_");
                System.out.println(url[1]);
                String hashValue= Hashing.sha256().hashString(url[1], StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:DETAIL:PENDING:"+url[0]+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:SUCCESS:"+url[0]+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:NOT_EXIST:"+url[0]+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:TRY:"+url[0]+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:PROCESSING:"+url[0]+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:DETAIL:PROCESSING:"+url[0]+":"+hashValue, url[0]+"_"+url[1]);
                    redisHelper.template.delete("SCRAPER:DETAIL:FAILED:"+url[0]+":"+hashValue);
                    PostDocument postDocument=new PostDocument();
                    PostStatus postStatus=webScraper.scrape(url[1], Integer.parseInt(url[0]), api_keys, postDocument);
                    redisHelper.bdsDetailPageRedisHandle(Integer.parseInt(url[0]), url[1], hashValue, postStatus, true);
                }
            });
        }
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
        f.thenApply(integer -> {
            executor.shutdown();
            System.out.println("Done");
            return null;
        });
    }
    public void runTryCase(int numOfItems, int numOfThread){
        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:DETAIL:TRY:*");
        List<String> values = ops.multiGet(keys);
        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfItems; i++) {
            String api_keys=apiKeyHelper.getApi_keys()[i % apiKeyHelper.getApi_keys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String[] url=ele.split("_");
                System.out.println(url[1]);
                String hashValue= Hashing.sha256().hashString(url[1], StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:DETAIL:PENDING:"+url[0]+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:SUCCESS:"+url[0]+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:NOT_EXIST:"+url[0]+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:FAILED:"+url[0]+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:PROCESSING:"+url[0]+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:DETAIL:PROCESSING:"+url[0]+":"+hashValue, url[0]+"_"+url[1]);
                    redisHelper.template.delete("SCRAPER:DETAIL:TRY:"+url[0]+":"+hashValue);
                    PostDocument postDocument=new PostDocument();
                    PostStatus postStatus=webScraper.scrape(url[1], Integer.parseInt(url[0]), api_keys, postDocument);
                    redisHelper.bdsDetailPageRedisHandle(Integer.parseInt(url[0]), url[1], hashValue, postStatus, false);
                }
            });
        }
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
        f.thenApply(integer -> {
            executor.shutdown();
            System.out.println("Done");
            return null;
        });
    }
    public void runTestCase(String url, String baseUrl) {
//        Document document=webScraper.loadPage(url, "ToQL0trA5Q5uefaaxLlq4g");
//        System.out.println(document);
        webScraper.scrape(url, 0, "7e9072c3a8c54d62799c3ac5276b1d49", new PostDocument());
//        webScraper.scrape(url, 0, "ToQL0trA5Q5uefaaxLlq4g");
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    @Autowired
    public void setLinksHelper(LinksHelper linksHelper) {this.linksHelper = linksHelper;}
    @Autowired
    public void setRedisHelper(RedisHelper redisHelper) {this.redisHelper = redisHelper;}
    @Autowired
    public void setWebScraper(WebScraperBDSDetailImpl webScraper) {
        this.webScraper = webScraper;
    }
    @Autowired
    public void setScraperWebServiceFactory(ScraperServiceFactory scraperServiceFactory) {
        this.scraperServiceFactory = scraperServiceFactory;
    }

}
