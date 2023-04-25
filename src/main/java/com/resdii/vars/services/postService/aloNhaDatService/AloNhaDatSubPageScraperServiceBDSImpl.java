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

    @Autowired
    public void setAloNhaDatBDSDetailPageScraperServiceTemplate(AloNhaDatBDSDetailScraperServiceTemplateImpl aloNhaDatBDSDetailPageScraperServiceTemplate) {
        this.aloNhaDatBDSDetailPageScraperServiceTemplate = aloNhaDatBDSDetailPageScraperServiceTemplate;
    }
}
