package com.resdii.vars.services.postService.nhaTotService;

import com.google.common.hash.Hashing;
import com.resdii.vars.services.postService.BDSWebSubPageScraperImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperByMeImpl;
import com.resdii.vars.utils.CommonUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.resdii.vars.constants.GlobalConstant.failedLoadPage;
import static com.resdii.vars.constants.GlobalConstant.hitCurrentPlan;
import static com.resdii.vars.utils.CommonUtils.findLinks;

@Component
public class NhaTotSubPageScraperServiceBDSImpl extends BDSWebSubPageScraperImpl {
    private int thresholdBreak=10000;
    private String notExist="Không có kết quả cho bộ lọc đã chọn";

    public NhaTotSubPageScraperServiceBDSImpl() {
        setBaseUrl("https://www.nhatot.com/");
    }

    @PostConstruct
    public void postConstructor(){
        setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperByMeImpl.class));
    }

    @Override
    public void getLinksByPostType(Integer command, String baseUrl, String prefix){
        int numOfThread=apiKeyHelper.getApi_key_for_load_paging().length*5;
        ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i <= numOfThread; i++) {
            int currentKeyIndex=i % apiKeyHelper.getApi_key_for_load_paging().length;
            String apiKey=apiKeyHelper.getApi_key_for_load_paging()[currentKeyIndex];
            int coefficient=i;
            executor.submit(()->{
                int index=0;
                while (true){
                    String urlWithPageIndex= linksHelper.getLinksPageIndex( baseUrl, command, coefficient+numOfThread*index);
                    String pageHashValue= Hashing.sha256().hashString(urlWithPageIndex, StandardCharsets.UTF_8).toString();
                    if(redisHelper.template.keys("SCRAPER:"+prefix+":PAGE:*:"+command+":"+pageHashValue).size()==0){
                        String htmlPage= webScraper.loadPage(urlWithPageIndex, apiKey).html();
                        if(htmlPage.contains(notExist) || index>thresholdBreak){
                            break;
                        }
                        if(htmlPage.contains(failedLoadPage) || htmlPage.contains(hitCurrentPlan)){
                            redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:FAILED:"+command+":"+pageHashValue, command+"_"+urlWithPageIndex);
                        }else{
                            redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":PAGE:SUCCESS:"+command+":"+pageHashValue,command+"_"+urlWithPageIndex );
                        }
                        savedDetailLink(findLinks(htmlPage, ".ListAds_ListAds__rEu_9.col-xs-12.no-padding"), command, baseUrl, prefix);
                    }
                    index++;
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
}
