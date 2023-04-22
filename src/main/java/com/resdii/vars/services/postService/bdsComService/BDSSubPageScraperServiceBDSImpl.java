package com.resdii.vars.services.postService.bdsComService;

import com.resdii.vars.services.postService.BDSWebSubPageScraperImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceCustomForBdsComImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class BDSSubPageScraperServiceBDSImpl extends BDSWebSubPageScraperImpl {
    private BDSComDetailServiceScraperTemplateImpl bdsComDetailPageServiceScraperService;

    public BDSSubPageScraperServiceBDSImpl() {
        setBaseUrl("https://batdongsan.com.vn/");
    }

    @PostConstruct
    public void postConstructor(){
        webScraper.setBdsDetailPageTemplate(bdsComDetailPageServiceScraperService);
        webScraper.setScraperWebService(scraperServiceFactory.getScraperWebService(ScraperServiceCustomForBdsComImpl.class));
    }

    @Autowired
    public void setBdsComDetailPageServiceScraperService(BDSComDetailServiceScraperTemplateImpl bdsComDetailPageServiceScraperService) {
        this.bdsComDetailPageServiceScraperService = bdsComDetailPageServiceScraperService;
    }
}
