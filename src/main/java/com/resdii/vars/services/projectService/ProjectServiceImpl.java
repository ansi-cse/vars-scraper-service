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
        int command=2;
        int listKeyLength=apiKeyHelper.getApi_key_for_load_paging().length;
        ExecutorService executor = Executors.newFixedThreadPool(listKeyLength*5);
        Integer numPage= linksHelper.getMaxPageIndex(baseUrl, command);
        for (int i = 1; i <= numPage; i++) {
            int currentKeyIndex=i % listKeyLength;
            String apiKey=apiKeyHelper.getApi_key_for_load_paging()[currentKeyIndex];
            Integer postPageIndex=i;
            executor.submit(()-> {
                String urlWithPageIndex= linksHelper.getLinksPageIndex( baseUrl, command, postPageIndex);
                String pageHashValue= Hashing.sha256().hashString(urlWithPageIndex, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:*:"+2+":"+pageHashValue).size()==0){
                    String htmlPage= projectScraper.loadPage(urlWithPageIndex, apiKey).html();
                    if(htmlPage.contains(failedLoadPage) || htmlPage.contains(hitCurrentPlan)){
                        redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":"+pageHashValue, command+"_"+urlWithPageIndex);
                    }else{
                        redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:SUCCESS:"+command+":"+pageHashValue,command+"_"+urlWithPageIndex );
                    }
                    List<String> listLink=findLinks(htmlPage, ".js__project-card.js__card-project-web.re__prj-card-full");
                    savedDetailLink(listLink, command, baseUrl, prefix);
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
        ExecutorService executor = Executors.newFixedThreadPool(apiKeyHelper.getApi_key_for_load_paging().length*5);
        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        String baseUrl="https://batdongsan.com.vn/";
        String prefix=GlobalConstant.baseUrlToPrefix.get(baseUrl);
        int command=2;
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":*");
        List<String> values = ops.multiGet(keys);
        for (int i = 0; i < values.size(); i++) {
            String api_key=apiKeyHelper.getApi_key_for_load_paging()[i % apiKeyHelper.getApi_key_for_load_paging().length];
            String ele=values.get(i);
            executor.submit(()->{
                String[] pagingURL=ele.split("_");
                String htmlPage= projectScraper.loadPage(pagingURL[1], api_key).html();
                String pageHashValue= Hashing.sha256().hashString(pagingURL[1], StandardCharsets.UTF_8).toString();
                if(!htmlPage.contains(failedLoadPage) && !htmlPage.contains(hitCurrentPlan)){
                    redisHelper.template.delete("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":"+pageHashValue);
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:SUCCESS:"+command+":"+pageHashValue,ele );
                    savedDetailLink(findLinks(htmlPage, ".js__project-card.js__card-project-web.re__prj-card-full"), command, baseUrl, prefix);
                }
            });
        }
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(new CommonUtils.CallBackForMultithreading(), executor);
        f.thenApply(integer -> {
            Set<String> failedKeys = redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":*");
            List<String> failedValue = ops.multiGet(failedKeys);
            if(failedValue.size()!=0){runGetLinksFailed();}
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
                count.set(count.get() + 1);
                redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PENDING:"+command+":"+hashValue, command+"_"+url);
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
        for (int i = 0; i < numOfItems; i++) {
            String api_key=apiKeyHelper.getApi_keys()[i % apiKeyHelper.getApi_keys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.replace("2_","");
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:FAILED:"+2+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+2+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+2+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:TRY:"+2+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+2+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+2+":"+hashValue, 2+"_"+url);
                    redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:PENDING:"+2+":"+hashValue);
                    ProjectDTO projectDTO=new ProjectDTO();
                    PostStatus postStatus= projectScraper.scrape(url, 2, api_key, projectDTO);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(2, url,prefix, hashValue, postStatus, false);
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
        int command=2;

        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:FAILED:*");
        List<String> values = ops.multiGet(keys);

        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfItems; i++) {
            String api_keys=apiKeyHelper.getApi_keys()[i % apiKeyHelper.getApi_keys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.replace("2_","");
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PENDING:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:TRY:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue, command+"_"+url);
                    redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:FAILED:"+command+":"+hashValue);
                    ProjectDTO projectDTO=new ProjectDTO();
                    PostStatus postStatus= projectScraper.scrape(url, command, api_keys, projectDTO);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(command, url, prefix, hashValue, postStatus, true);
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
        int command=2;

        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:TRY:*");
        List<String> values = ops.multiGet(keys);

        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfItems; i++) {
            String api_keys=apiKeyHelper.getApi_keys()[i % apiKeyHelper.getApi_keys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.replace("2_","");
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PENDING:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:FAILED:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue, command+"_"+url);
                    redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:TRY:"+command+":"+hashValue);
                    ProjectDTO projectDTO=new ProjectDTO();
                    PostStatus postStatus= projectScraper.scrape(url, command, api_keys, projectDTO);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(command, url, prefix, hashValue, postStatus, false);
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
        int command=2;

        ValueOperations<String, String> ops = redisHelper.template.opsForValue();
        Set<String> keys = redisHelper.template.keys("SCRAPER:"+prefix+":DETAIL:PROCESSING:*");
        List<String> values = ops.multiGet(keys);

        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfItems; i++) {
            String api_keys=apiKeyHelper.getApi_keys()[i % apiKeyHelper.getApi_keys().length];
            String ele=values.get(i);
            executor.submit(()->{
                String url=ele.replace("2_","");
                String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
                if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:PENDING:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:NOT_EXIST:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:FAILED:"+command+":"+hashValue)==null
                        && redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:TRY:"+command+":"+hashValue)==null){
                    redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:FAILED:"+command+":"+hashValue, command+"_"+url);
                    redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:PROCESSING:"+command+":"+hashValue);
                    ProjectDTO projectDTO=new ProjectDTO();
                    PostStatus postStatus= projectScraper.scrape(url, command, api_keys, projectDTO);
                    redisHelper.bdsDetailPageRedisHandleWithPrefix(command, url, prefix, hashValue, postStatus, false);
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
    public void getProject(String url,String api_key){
        ProjectDTO projectDTO=new ProjectDTO();
        projectScraper.scrape(url, 2, api_key, projectDTO);
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
