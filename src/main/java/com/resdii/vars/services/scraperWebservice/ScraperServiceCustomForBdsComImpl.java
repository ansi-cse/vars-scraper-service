package com.resdii.vars.services.scraperWebservice;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import static com.resdii.vars.constants.GlobalConstant.failedLoadPage;

@Service
public class ScraperServiceCustomForBdsComImpl extends ScraperServiceScraperByMeImpl {

    @Override
    public Document loadPage(String url, String api_key) {
        try {
            String html= scraperByMeApiClient.getHtmlForBdsCom(url).getBody();
            return Jsoup.parse(html);
        } catch (Exception e) {
            return Jsoup.parse(failedLoadPage);
        }
    }


    public Document loadInvestor(String url) {
        try {
            String html= scraperByMeApiClient.getHtmlForBdsComInvestor(url).getBody();
            return Jsoup.parse(html);
        } catch (Exception e) {
            return Jsoup.parse(failedLoadPage);
        }
    }
}
