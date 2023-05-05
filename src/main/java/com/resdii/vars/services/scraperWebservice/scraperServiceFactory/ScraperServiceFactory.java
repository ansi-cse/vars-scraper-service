package com.resdii.vars.services.scraperWebservice.scraperServiceFactory;

import com.resdii.vars.services.scraperWebservice.ScraperServiceCustomForBdsComImpl;
import com.resdii.vars.services.scraperWebservice.ScraperService;
import com.resdii.vars.services.scraperWebservice.ScraperServiceCrawlBaseImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperApiImpl;
import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperByMeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ScraperServiceFactory {
    ScraperServiceScraperApiImpl scraperServiceScraperApiImplApi;
    ScraperServiceCrawlBaseImpl scraperServiceCrawlBaseImpl;
    ScraperServiceScraperByMeImpl scraperServiceScraperByMeImpl;
    ScraperServiceCustomForBdsComImpl scraperServiceCustomForBdsComImpl;

    public ScraperService getScraperWebService(Class<?> type){
        ScraperService scraperService = null;
        if (type.equals(ScraperServiceScraperApiImpl.class)) {
            scraperService = scraperServiceScraperApiImplApi;
        } else if (type.equals(ScraperServiceCrawlBaseImpl.class)) {
            scraperService = scraperServiceCrawlBaseImpl;
        } else if (type.equals(ScraperServiceScraperByMeImpl.class)) {
            scraperService = scraperServiceScraperByMeImpl;
        } else if (type.equals(ScraperServiceCustomForBdsComImpl.class)) {
            scraperService = scraperServiceCustomForBdsComImpl;
        }
        return scraperService;
    }

    @Autowired
    public void setScraperApi(ScraperServiceScraperApiImpl scraperServiceScraperApiImplApi) {this.scraperServiceScraperApiImplApi = scraperServiceScraperApiImplApi;}
    @Autowired
    public void setCrawlBase(ScraperServiceCrawlBaseImpl scraperServiceCrawlBaseImpl) {this.scraperServiceCrawlBaseImpl = scraperServiceCrawlBaseImpl;}

    @Autowired
    public void setScraperServiceScraperByMeImpl(ScraperServiceScraperByMeImpl scraperServiceScraperByMeImpl) {
        this.scraperServiceScraperByMeImpl = scraperServiceScraperByMeImpl;
    }
    @Autowired
    public void setCustomForBdsCom(ScraperServiceCustomForBdsComImpl scraperServiceCustomForBdsComImpl) {this.scraperServiceCustomForBdsComImpl = scraperServiceCustomForBdsComImpl;}
}
