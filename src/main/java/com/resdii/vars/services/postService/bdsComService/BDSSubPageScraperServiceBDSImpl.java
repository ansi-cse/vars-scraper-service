package com.resdii.vars.services.postService.bdsComService;

import com.resdii.vars.services.postService.BDSWebSubPageScraperImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceCustomForBdsComImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperByMeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Component
public class BDSSubPageScraperServiceBDSImpl extends BDSWebSubPageScraperImpl {
    private BDSComDetailServiceScraperTemplate bdsComDetailPageServiceScraperService;

    public BDSSubPageScraperServiceBDSImpl() {
        setBaseUrl("https://batdongsan.com.vn/");
    }

    @PostConstruct
    public void postConstructor(){
        webScraper.setBdsDetailPageTemplate(bdsComDetailPageServiceScraperService);
        webScraper.setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceCustomForBdsComImpl.class));
        setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceScraperByMeImpl.class));
    }

    @Autowired
    public void setBdsComDetailPageServiceScraperService(BDSComDetailServiceScraperTemplate bdsComDetailPageServiceScraperService) {
        this.bdsComDetailPageServiceScraperService = bdsComDetailPageServiceScraperService;
    }
}
