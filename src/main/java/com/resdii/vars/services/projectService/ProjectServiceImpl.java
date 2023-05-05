package com.resdii.vars.services.projectService;

import com.google.common.hash.Hashing;
import com.resdii.vars.constants.GlobalConstant;
import com.resdii.vars.dto.ProjectDTO;
import com.resdii.vars.enums.PostStatus;
import com.resdii.vars.helper.ApiKeyHelper;
import com.resdii.vars.helper.LinksHelper;
import com.resdii.vars.helper.RedisHelper;
import com.resdii.vars.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.resdii.vars.constants.GlobalConstant.failedLoadPage;
import static com.resdii.vars.constants.GlobalConstant.hitCurrentPlan;
import static com.resdii.vars.utils.CommonUtils.findLinks;

@Service
public class ProjectServiceImpl implements ProjectService{
    WebScraperProjectImpl projectScraper;
    RedisTemplate redisTemplate;
    ApiKeyHelper apiKeyHelper;
    LinksHelper linksHelper;
    RedisHelper redisHelper;

    public void runGetLinks(){
        String baseUrl="https://batdongsan.com.vn/";
        String prefix=GlobalConstant.baseUrlToPrefix.get(baseUrl);
        String postType="";
        int listKeyLength=apiKeyHelper.getApiKeyForLoadPaging().length;
        ExecutorService executor = Executors.newFixedThreadPool(listKeyLength*5);
        Integer numPage= linksHelper.getMaxPageIndex(baseUrl, postType);
        for (int i = 1; i <= numPage; i++) {
            int currentKeyIndex=i % listKeyLength;
            String apiKey=apiKeyHelper.getApiKeyForLoadPaging()[currentKeyIndex];
            Integer postPageIndex=i;
            executor.submit(()-> {
                String urlWithPageIndex= linksHelper.getLinksPageIndex( baseUrl, postType, postPageIndex);
                String pageHashValue= Hashing.sha256().hashString(urlWithPageIndex, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:*:"+2+":"+pageHashValue).size()==0){
                    String htmlPage= projectScraper.loadPage(urlWithPageIndex, apiKey).html();
                    if(htmlPage.contains(failedLoadPage) || htmlPage.contains(hitCurrentPlan)){
                        redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:FAILED:"+postType+":"+pageHashValue, postType+"_"+urlWithPageIndex);
                    }else{
                        redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:SUCCESS:"+postType+":"+pageHashValue,postType+"_"+urlWithPageIndex );
                    }
                    List<String> listLink=findLinks(htmlPage, ".js__project-card.js__card-project-web.re__prj-card-full");
                    savedDetailLink(listLink, postType, baseUrl, prefix);
                }
            });
        }
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
        f.thenApply(integer -> {
            runGetLinksFailed();
            System.out.println("Done Get Link");
            return null;
        });
    }
    public void runGetLinksFailed(){
        ExecutorService executor = Executors.newFixedThreadPool(apiKeyHelper.getApiKeyForLoadPaging().length*5);
        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        String baseUrl="https://batdongsan.com.vn/";
        String prefix=GlobalConstant.baseUrlToPrefix.get(baseUrl);
        String postType="";
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:FAILED:"+postType+":*");
        List<String> values = ops.multiGet(keys);
        for (int i = 0; i < values.size(); i++) {
            String api_key=apiKeyHelper.getApiKeyForLoadPaging()[i % apiKeyHelper.getApiKeyForLoadPaging().length];
            String ele=values.get(i);
            executor.submit(()->{
                String[] pagingURL=ele.split("_");
                String htmlPage= projectScraper.loadPage(pagingURL[1], api_key).html();
                String pageHashValue= Hashing.sha256().hashString(pagingURL[1], StandardCharsets.UTF_8).toString();
                if(!htmlPage.contains(failedLoadPage) && !htmlPage.contains(hitCurrentPlan)){
                    redisHelper.template.delete("SCRAPER:"+prefix+":PAGE:FAILED:"+postType+":"+pageHashValue);
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:SUCCESS:"+postType+":"+pageHashValue,ele );
                    savedDetailLink(findLinks(htmlPage, ".js__project-card.js__card-project-web.re__prj-card-full"), postType, baseUrl, prefix);
                }
            });
        }
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
        f.thenApply(integer -> {
            Set<String> failedKeys = redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:FAILED:"+postType+":*");
            List<String> failedValue = ops.multiGet(failedKeys);
            if(failedValue.size()!=0){runGetLinksFailed();}
            else{
                executor.shutdown();
                System.out.println("Done");
            }
            return null;
        });
    }
    public int savedDetailLink(List<String> listLinks, String postType, String baseUrl, String prefix){
        AtomicInteger count= new AtomicInteger();
        listLinks.forEach(detailUrl->{
            String url= baseUrl +detailUrl;
            String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
            if(redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:*:"+postType+":"+hashValue).size()==0){
                count.set(count.get() + 1);
                redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PENDING:"+postType+":"+hashValue, postType+"_"+url);
            }
        });
        return count.get();
    }
    public void runGetDetailProject(int numOfItems, int numOfThread){
        String baseUrl="https://batdongsan.com.vn/";
        String prefix=GlobalConstant.baseUrlToPrefix.get(baseUrl);
        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:PENDING:2:*");
        List<String> values = ops.multiGet(keys);
        String postType="BDS_PROJECT";
        for (int i = 0; i < numOfItems; i++) {
            String api_key=apiKeyHelper.getApiKeys()[i % apiKeyHelper.getApiKeys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.replace("2_","");
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+postType+":"+prefix+":DETAIL:FAILED:"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+postType+":"+prefix+":DETAIL:SUCCESS:"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+postType+":"+prefix+":DETAIL:NOT_EXIST:"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+postType+":"+prefix+":DETAIL:TRY:"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+postType+":"+prefix+":DETAIL:PROCESSING:"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+postType+":"+prefix+":DETAIL:PROCESSING:"+hashValue, url);
                    redisHelper.template.delete("SCRAPER:"+postType+":"+prefix+":DETAIL:PENDING:"+hashValue);
                    ProjectDTO projectDTO=new ProjectDTO();
                    PostStatus postStatus= projectScraper.scrape(url, postType, prefix, api_key, projectDTO);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(postType, url,prefix, hashValue, postStatus);
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
    public void runGetDetailProjectFailed(int numOfItems, int numOfThread){
        String baseUrl="https://batdongsan.com.vn/";
        String prefix=GlobalConstant.baseUrlToPrefix.get(baseUrl);
        String postType="BDS_PROJECT";

        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:FAILED:*");
        List<String> values = ops.multiGet(keys);

        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfItems; i++) {
            String api_keys=apiKeyHelper.getApiKeys()[i % apiKeyHelper.getApiKeys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.replace("2_","");
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PENDING:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:TRY:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+postType+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+postType+":"+hashValue, postType+"_"+url);
                    redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:FAILED:"+postType+":"+hashValue);
                    ProjectDTO projectDTO=new ProjectDTO();
                    PostStatus postStatus= projectScraper.scrape(url, postType, prefix, api_keys, projectDTO);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(postType, url, prefix, hashValue, postStatus);
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
    public void runGetDetailProjectFailedTry(int numOfItems, int numOfThread){
        String baseUrl="https://batdongsan.com.vn/";
        String prefix=GlobalConstant.baseUrlToPrefix.get(baseUrl);
        String postType="BDS_PROJECT";

        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:TRY:*");
        List<String> values = ops.multiGet(keys);

        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfItems; i++) {
            String api_keys=apiKeyHelper.getApiKeys()[i % apiKeyHelper.getApiKeys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.replace("2_","");
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PENDING:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:FAILED:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+postType+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+postType+":"+hashValue, postType+"_"+url);
                    redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:TRY:"+postType+":"+hashValue);
                    ProjectDTO projectDTO=new ProjectDTO();
                    PostStatus postStatus= projectScraper.scrape(url, postType, prefix, api_keys, projectDTO);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(postType, url, prefix, hashValue, postStatus);
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
    public void runGetDetailProjectProcessing(int numOfItems, int numOfThread){
        String baseUrl="https://batdongsan.com.vn/";
        String prefix=GlobalConstant.baseUrlToPrefix.get(baseUrl);
        String postType="BDS_PROJECT";

        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:PROCESSING:*");
        List<String> values = ops.multiGet(keys);

        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfItems; i++) {
            String api_keys=apiKeyHelper.getApiKeys()[i % apiKeyHelper.getApiKeys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.replace("2_","");
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PENDING:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:FAILED:"+postType+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:TRY:"+postType+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:FAILED:"+postType+":"+hashValue, postType+"_"+url);
                    redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+postType+":"+hashValue);
                    ProjectDTO projectDTO=new ProjectDTO();
                    PostStatus postStatus= projectScraper.scrape(url, postType, prefix, api_keys, projectDTO);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(postType, url, prefix, hashValue, postStatus);
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
    public void updateProjectDTO(String projectTable){
        projectScraper.updateProjectDTO(projectTable);
    }
    public void concatFile(){
        projectScraper.concatFile();
    }

    @Autowired
    public void setProjectTemplateScraper(WebScraperProjectImpl projectScraper) {
        this.projectScraper = projectScraper;
    }
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Autowired
    public void setApiKeyHelper(ApiKeyHelper apiKeyHelper) {
        this.apiKeyHelper = apiKeyHelper;
    }
    @Autowired
    public void setLinksHelper(LinksHelper linksHelper) {
        this.linksHelper = linksHelper;
    }
    @Autowired
    public void setRedisHelper(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }
}
