package com.resdii.vars.services.scraperWebservice;

import com.resdii.vars.api.ScraperByMeApiClient;
import com.resdii.vars.enums.PostStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.resdii.vars.constants.GlobalConstant.failedLoadPage;

@Service
public class ScraperServiceScraperByMeImpl implements ScraperService {

    protected ScraperByMeApiClient scraperByMeApiClient;

    public Document loadPage(String url, String api_key){
        try {
            String html= scraperByMeApiClient.getHtml(url).getBody();
            return Jsoup.parse(html);
        } catch (Exception e) {
            return Jsoup.parse(failedLoadPage);
        }
    };

    @Override
    public PostStatus preHandleDataForScraper(Document document) {
        return PostStatus.SUCCESS;
    }

    @Autowired
    public void setScraperByMeClient(ScraperByMeApiClient scraperByMeApiClient) {
        this.scraperByMeApiClient = scraperByMeApiClient;
    }
}
