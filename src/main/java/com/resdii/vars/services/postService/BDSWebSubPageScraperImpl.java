package com.resdii.vars.services.postService;

import com.google.common.hash.Hashing;
import com.resdii.vars.api.ScheduleApiClient;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.PostDocument;
import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.enums.PostType;
import com.resdii.vars.helper.LinksHelper;
import com.resdii.vars.helper.RedisHelper;
import com.resdii.vars.services.scraperWebservice.ScraperServiceCustomForBdsComImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperByMeImpl;
import com.resdii.vars.services.scraperWebservice.scraperServiceFactory.ScraperServiceFactory;
import com.resdii.vars.services.WebBaseScraperImpl;
import com.resdii.vars.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.resdii.vars.constants.GlobalConstant.failedLoadPage;
import static com.resdii.vars.constants.GlobalConstant.hitCurrentPlan;
import static com.resdii.vars.utils.CommonUtils.findLinks;

public class BDSWebSubPageScraperImpl extends WebBaseScraperImpl {

    protected String baseUrl;
    protected WebScraperBDSDetail webScraper;
    protected ScraperServiceFactory scraperServiceFactory;
    protected LinksHelper linksHelper;
    protected RedisHelper redisHelper;
    protected ScheduleApiClient scheduleApiClient;

    public void getLinks(String postType, String baseUrl, int numOfPage){
        String prefix=GlobalConstant.baseUrlToPrefix.get(baseUrl);
        int listKeyLength=apiKeyHelper.getApiKeyForLoadPaging().length;
        ExecutorService executor = Executors.newFixedThreadPool(100);

        int numPage=(numOfPage != -1) ? numOfPage : linksHelper.getMaxPageIndex(baseUrl, postType);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            int currentKeyIndex=i % listKeyLength;
            String apiKey=apiKeyHelper.getApiKeyForLoadPaging()[currentKeyIndex];
            Integer postPageIndex=i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    String urlWithPageIndex= linksHelper.getLinksPageIndex( baseUrl, postType, postPageIndex);
                    String pageHashValue= Hashing.sha256().hashString(urlWithPageIndex, StandardCharsets.UTF_8).toString();
                    String keyFailedForRedis="SCRAPER:"+postType+":"+prefix+":PAGE:FAILED:"+pageHashValue;
                    String keySuccessForRedis="SCRAPER:"+postType+":"+prefix+":PAGE:SUCCESS:"+pageHashValue;
                    String valueForRedis=urlWithPageIndex;

                    if (redisHelper.template.keys("SCRAPER:"+postType+":"+ prefix + ":PAGE:*:" + pageHashValue).size()!=0) {
                        return;
                    }

                    String htmlPage= loadPage(urlWithPageIndex, apiKey).html();
                    savedDetailLink(findLinks(htmlPage, GlobalConstant.baseUrlToClassNameForGetLinks.get(baseUrl)), postType, baseUrl, prefix);
                    if(htmlPage.contains(failedLoadPage) || htmlPage.contains(hitCurrentPlan)){
                        redisHelper.template.opsForValue().set(keyFailedForRedis, valueForRedis);
                    }else{
                        redisHelper.template.opsForValue().set(keySuccessForRedis, valueForRedis);
                    }
                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }, executor);
            futures.add(future);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenRun(() -> {
            scheduleApiClient.notifyComplete("LINKS",baseUrl, prefix, postType, "DEV");
            System.out.println("Done Get Link");
        });
        // Wait for all tasks to complete
        allFutures.join();

        executor.shutdown();
    }

    public int savedDetailLink(List<String> listLinks, String postType, String baseUrl, String prefix){
        AtomicInteger count= new AtomicInteger();
        listLinks.forEach(detailUrl->{
            String url= baseUrl +detailUrl;
            String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
            if(redisHelper.template.keys("*:"+hashValue).size()==0){
                System.out.println(url);
                count.set(count.get() + 1);
                redisHelper.template.opsForValue().set("SCRAPER:"+postType+":"+prefix+":DETAIL:PENDING:"+hashValue, url);
            }
        });
        return count.get();
    }

    public void getDetailPage(String prefix, String postType, int numOfItems, int numOfThread ){
        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        try{
            ValueOperations<String, String> ops = redisHelper.template.opsForValue();
            Set keysPending = redisHelper.template.keys("SCRAPER:"+postType+":"+prefix+":DETAIL:PENDING:*");
            Set keysFailed = redisHelper.template.keys("SCRAPER:"+postType+":"+prefix+":DETAIL:FAILED:*");
            keysPending.addAll(ops.multiGet(keysFailed));
            List<String> values = ops.multiGet(keysPending);
            String detailKeyPrefix = "SCRAPER:"+postType+":"+prefix+":DETAIL:";
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int i = 0; i < numOfItems; i++) {
                if(values.size()<=i){
                    break;
                }
                String apiKey=apiKeyHelper.getApiKeys()[i % apiKeyHelper.getApiKeys().length];
                String ele=values.get(i);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try{
                        String url=ele;
                        String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                        if(redisHelper.template.opsForValue().multiGet(Arrays.asList(
                                detailKeyPrefix + "FAILED:" + hashValue,
                                detailKeyPrefix + "SUCCESS:" + hashValue,
                                detailKeyPrefix + "NOT_EXIST:" + hashValue,
                                detailKeyPrefix + "TRY:" + hashValue,
                                detailKeyPrefix + "PROCESSING:" + hashValue
                        )).stream().allMatch(Objects::isNull)){
                            redisHelper.template.opsForValue().set("SCRAPER:"+postType+":"+prefix+":DETAIL:PROCESSING:"+hashValue, url);
                            redisHelper.template.delete("SCRAPER:"+postType+":"+prefix+":DETAIL:PENDING:"+hashValue);
                            PostDocument postDocument=new PostDocument();
                            PostStatus postStatus=webScraper.scrape(url, postType, prefix,  apiKey, postDocument);
                            redisHelper.bdsDetailPageRedisHandleWithPrefix(postType, url, prefix , hashValue, postStatus);
                        }
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }
                }, executor);
                futures.add(future);
            }
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.thenRun(() -> {
                scheduleApiClient.notifyComplete("DETAIL",baseUrl,prefix, postType, "DEV");
                System.out.println("Done Get Link");
            });
            // Wait for all tasks to complete
            allFutures.join();

            executor.shutdown();
        } finally {
            executor.shutdown();
        }
    }

//    public void getLinksByPostTypeFailed(String postType, String baseUrl,String prefix) {
//        ExecutorService executor = Executors.newFixedThreadPool(apiKeyHelper.getApiKeyForLoadPaging().length*5);
//        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
//        Set<String> keys = redisHelper.template.keys("SCRAPER:"+postType+":"+prefix+":PAGE:FAILED:*");
//        List<String> values = ops.multiGet(keys);
//        for (int i = 0; i < values.size(); i++) {
//            String api_key=apiKeyHelper.getApiKeyForLoadPaging()[i % apiKeyHelper.getApiKeyForLoadPaging().length];
//            String url=values.get(i);
//            executor.submit(()->{
//                String htmlPage= loadPage(url, api_key).html();
//                String pageHashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
//                if(!htmlPage.contains(failedLoadPage) && !htmlPage.contains(hitCurrentPlan)){
//                    redisHelper.template.delete("SCRAPER:"+postType+":"+prefix+":PAGE:FAILED:"+pageHashValue);
//                    redisHelper.template.opsForValue().set("SCRAPER:"+postType+":"+prefix+":PAGE:SUCCESS:"+pageHashValue, url );
//                    savedDetailLink(findLinks(htmlPage, GlobalConstant.baseUrlToClassNameForGetLinks.get(baseUrl)), postType, baseUrl, prefix);
//                }
//            });
//        }
//        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
//        f.thenApply(integer -> {
//            Set<String> failedKeys = redisHelper.template.keys("SCRAPER:"+postType+":"+prefix+":PAGE:FAILED:*");
//            List<String> failedValue = ops.multiGet(failedKeys);
//            if(failedValue.size()!=0){getLinksByPostTypeFailed(postType, baseUrl, prefix);}
//            else{
////                scheduleApiClient.notifyComplete(baseUrl);
//                executor.shutdown();
//                System.out.println("Done");
//            }
//            return null;
//        });
//    }


//    public void runFailedCase(int numOfItems, int numOfThread, String postType){
//        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
//        Set<String> keys = redisHelper.template.keys("SCRAPER:DETAIL:FAILED:*");
//        List<String> values = ops.multiGet(keys);
//        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
//        for (int i = 0; i < numOfItems; i++) {
//            String api_keys=apiKeyHelper.getApiKeys()[i % apiKeyHelper.getApiKeys().length];
//            String ele=values.get(i);
//            executor.submit(()->{
//                String[] url=ele.split("_");
//                System.out.println(url[1]);
//                String hashValue= Hashing.sha256().hashString(url[1], StandardCharsets.UTF_8).toString();
//                if(redisHelper.template.opsForValue().get("SCRAPER:DETAIL:PENDING:"+url[0]+":"+hashValue)==null
//                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:SUCCESS:"+url[0]+":"+hashValue)==null
//                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:NOT_EXIST:"+url[0]+":"+hashValue)==null
//                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:TRY:"+url[0]+":"+hashValue)==null
//                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:PROCESSING:"+url[0]+":"+hashValue)==null){
//                    redisHelper.template.opsForValue().set("SCRAPER:DETAIL:PROCESSING:"+url[0]+":"+hashValue, url[0]+"_"+url[1]);
//                    redisHelper.template.delete("SCRAPER:DETAIL:FAILED:"+url[0]+":"+hashValue);
//                    PostDocument postDocument=new PostDocument();
//                    PostStatus postStatus=webScraper.scrape(url[1], postType, api_keys, postDocument);
//                    redisHelper.bdsDetailPageRedisHandle(Integer.parseInt(url[0]), url[1], hashValue, postStatus, true);
//                }
//            });
//        }
//        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
//        f.thenApply(integer -> {
//            executor.shutdown();
//            System.out.println("Done");
//            return null;
//        });
//    }
//    public void runTryCase(int numOfItems, int numOfThread){
//        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
//        Set<String> keys = redisHelper.template.keys("SCRAPER:DETAIL:TRY:*");
//        List<String> values = ops.multiGet(keys);
//        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
//        for (int i = 0; i < numOfItems; i++) {
//            String api_keys=apiKeyHelper.getApiKeys()[i % apiKeyHelper.getApiKeys().length];
//            String ele=values.get(i);
//            executor.submit(()->{
//                String[] url=ele.split("_");
//                System.out.println(url[1]);
//                String hashValue= Hashing.sha256().hashString(url[1], StandardCharsets.UTF_8).toString();
//                if(redisHelper.template.opsForValue().get("SCRAPER:DETAIL:PENDING:"+url[0]+":"+hashValue)==null
//                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:SUCCESS:"+url[0]+":"+hashValue)==null
//                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:NOT_EXIST:"+url[0]+":"+hashValue)==null
//                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:FAILED:"+url[0]+":"+hashValue)==null
//                        && redisHelper.template.opsForValue().get("SCRAPER:DETAIL:PROCESSING:"+url[0]+":"+hashValue)==null){
//                    redisHelper.template.opsForValue().set("SCRAPER:DETAIL:PROCESSING:"+url[0]+":"+hashValue, url[0]+"_"+url[1]);
//                    redisHelper.template.delete("SCRAPER:DETAIL:TRY:"+url[0]+":"+hashValue);
//                    PostDocument postDocument=new PostDocument();
//                    PostStatus postStatus=webScraper.scrape(url[1], Integer.parseInt(url[0]), api_keys, postDocument);
//                    redisHelper.bdsDetailPageRedisHandle(Integer.parseInt(url[0]), url[1], hashValue, postStatus, false);
//                }
//            });
//        }
//        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
//        f.thenApply(integer -> {
//            executor.shutdown();
//            System.out.println("Done");
//            return null;
//        });
//    }
//    public void runTestCase(String url, String baseUrl) {
//        webScraper.scrape(url, "BDS_SALE", "7e9072c3a8c54d62799c3ac5276b1d49", new PostDocument());
//    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    @Autowired
    public void setLinksHelper(LinksHelper linksHelper) {this.linksHelper = linksHelper;}
    @Autowired
    public void setRedisHelper(RedisHelper redisHelper) {this.redisHelper = redisHelper;}
    @Autowired
    public void setWebScraper(WebScraperBDSDetail webScraper) {
        this.webScraper = webScraper;
    }
    @Autowired
    public void setScraperWebServiceFactory(ScraperServiceFactory scraperServiceFactory) {this.scraperServiceFactory = scraperServiceFactory;}
    @Autowired
    public void setScheduleApiClient(ScheduleApiClient scheduleApiClient) {this.scheduleApiClient = scheduleApiClient;}
}
