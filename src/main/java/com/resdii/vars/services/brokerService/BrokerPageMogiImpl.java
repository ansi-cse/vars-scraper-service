package com.resdii.vars.services.brokerService;

import com.resdii.vars.services.scraperWebservice.ScraperServiceScraperByMeImpl;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ANSI.
 */

@Service
public class BrokerPageMogiImpl implements BrokerPage {
    String baseUrl="https://mogi.vn/moi-gioi/";

    ScraperServiceScraperByMeImpl scraperServiceScraperByMeImpl;

    @Override
    public void getLinks() {

    }

    @Override
    public void getDetail(String type) {

    }

//    @Override
//    public void update(String type) {
//
//    }

    public String pageIndex(int index) {
        return baseUrl+"?cp="+index;
    }

    public int countPage() {
        String pageUrl=pageIndex(1000);
        Document document= scraperServiceScraperByMeImpl.loadPage(pageUrl, null);
        Elements elements=document.select(".pagination li");
        return Integer.parseInt(elements.get(elements.size()-2).text());
    }

    @Autowired
    public void setScraperByMe(ScraperServiceScraperByMeImpl scraperServiceScraperByMeImpl) {
        this.scraperServiceScraperByMeImpl = scraperServiceScraperByMeImpl;
    }
}
