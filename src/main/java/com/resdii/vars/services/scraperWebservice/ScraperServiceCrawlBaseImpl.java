package com.resdii.vars.services.scraperWebservice;

import com.resdii.vars.enums.PostStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import static com.resdii.vars.constants.GlobalConstant.failedLoadPage;

@Service
public class ScraperServiceCrawlBaseImpl implements ScraperService {
    private String scraperUrl ="https://api.crawlbase.com/";

    public Document loadPage(String url, String api_key){
        try {
            String urlToFetch= scraperUrl
                    .concat("?token=")
                    .concat(api_key)
                    .concat("&url=")
                    .concat(url);
            return Jsoup.connect(urlToFetch)
                    .timeout(10000)
                    .proxy("127.0.0.1", 8888)
                    .get();
        } catch (Exception e) {
            return Jsoup.parse(failedLoadPage);
        }
    };
    @Override
    public PostStatus preHandleDataForParser(Document document) {
        if(document.html().contains(failedLoadPage)){
            System.out.println(failedLoadPage);
            return PostStatus.FAILED;
        }
        return PostStatus.SUCCESS;
    }
}
