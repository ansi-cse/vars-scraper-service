package com.resdii.vars.services.postService.aloNhaDatService;

import com.google.common.hash.Hashing;
import com.resdii.vars.services.postService.BDSWebSubPageScraperImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.resdii.vars.utils.CommonUtils.findLinks;


@Service
public class AloNhaDatSubPageScraperServiceBDSImpl extends BDSWebSubPageScraperImpl {

    private AloNhaDatBDSDetailScraperServiceTemplateImpl aloNhaDatBDSDetailPageScraperServiceTemplate;

    public AloNhaDatSubPageScraperServiceBDSImpl() {
        setBaseUrl("https://alonhadat.com.vn/");
    }
    @PostConstruct
    public void postConstructor(){
        webScraper.setBdsDetailPageTemplate(aloNhaDatBDSDetailPageScraperServiceTemplate);
        webScraper.setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperApiImpl.class));
    }

    public void updateGetDetailFailed(Integer command, String baseUrl, String prefix){
//        https://alonhadat.com.vn/nha-dat/can-ban/nha-dat/4/hai-phong.html
//        https://alonhadat.com.vn/nha-dat/can-ban/nha-dat/21/dak-nong.html
//        https://alonhadat.com.vn/nha-dat/can-ban/nha-dat/5/can-tho.html
        ExecutorService executor = Executors.newFixedThreadPool(apiKeyHelper.getApi_key_for_load_paging().length*5);
        Integer numPage= 10; //Dak Nong
        for (int i = 0; i < numPage; i++) {
            int currentKeyIndex=i % apiKeyHelper.getApi_key_for_load_paging().length;
            String apiKey=apiKeyHelper.getApi_key_for_load_paging()[currentKeyIndex];
            Integer postPageIndex=i;
            executor.submit(()->{
                String urlWithPageIndex= "https://alonhadat.com.vn/nha-dat/can-ban/nha-dat/21/dak-nong.html";
                if (postPageIndex > 0) {
                    urlWithPageIndex= urlWithPageIndex.replace(".html", "/trang--"+postPageIndex+".html");
                }
                String htmlPage= webScraper.loadPage(urlWithPageIndex, apiKey).html();
                List<String> listLinks=findLinks(htmlPage, ".ct_title");
                updateDetailLink(listLinks, command, baseUrl, prefix);
            });
        }
    }
    public void updateDetailLink(List<String> listLinks, Integer command, String baseUrl, String prefix){
        listLinks.forEach(detailUrl->{
            String url= baseUrl +detailUrl;
            String hashValue= Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
            if(redisHelper.template.opsForValue().get("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+command+":"+hashValue)!=null){
                redisHelper.template.opsForValue().set("SCRAPER:"+prefix+":DETAIL:PENDING:"+command+":"+hashValue, command+"_"+url);
                redisHelper.template.delete("SCRAPER:"+prefix+":DETAIL:SUCCESS:"+command+":"+hashValue);
            }
        });
    }

    @Autowired
    public void setAloNhaDatBDSDetailPageScraperServiceTemplate(AloNhaDatBDSDetailScraperServiceTemplateImpl aloNhaDatBDSDetailPageScraperServiceTemplate) {
        this.aloNhaDatBDSDetailPageScraperServiceTemplate = aloNhaDatBDSDetailPageScraperServiceTemplate;
    }
}
