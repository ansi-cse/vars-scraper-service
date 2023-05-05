package com.resdii.vars.services.postService.aloNhaDatService;

import com.resdii.vars.services.postService.BDSWebSubPageScraperImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceCustomForBdsComImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Component
public class AloNhaDatSubPageScraperServiceBDSImpl extends BDSWebSubPageScraperImpl {

    private AloNhaDatBDSDetailScraperServiceTemplate aloNhaDatBDSDetailPageScraperServiceTemplate;

    public AloNhaDatSubPageScraperServiceBDSImpl() {
        setBaseUrl("https://alonhadat.com.vn/");
    }
    @PostConstruct
    public void postConstructor(){
        webScraper.setBdsDetailPageTemplate(aloNhaDatBDSDetailPageScraperServiceTemplate);
        webScraper.setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperApiImpl.class));
        setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperApiImpl.class));
    }

    @Autowired
    public void setAloNhaDatBDSDetailPageScraperServiceTemplate(AloNhaDatBDSDetailScraperServiceTemplate aloNhaDatBDSDetailPageScraperServiceTemplate) {
        this.aloNhaDatBDSDetailPageScraperServiceTemplate = aloNhaDatBDSDetailPageScraperServiceTemplate;
    }
}
