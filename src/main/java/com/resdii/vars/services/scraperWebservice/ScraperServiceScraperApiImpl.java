package com.resdii.vars.services.scraperWebservice;

import com.resdii.vars.enums.PostStatus;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import static com.resdii.vars.constants.GlobalConstant.*;

@Service
public class ScraperServiceScraperApiImpl implements ScraperService {
    private String scraper_url="http://api.scraperapi.com";

    public Document loadPage(String url, String api_key){
        try {
            String urlToFetch=scraper_url
                    .concat("?api_key=")
                    .concat(api_key)
                    .concat("&url=")
                    .concat(url);
            return Jsoup.connect(urlToFetch)
                    .timeout(0)
                    .get();
        }catch (HttpStatusException httpStatusException){
            if(httpStatusException.getStatusCode()==403){
                return Jsoup.parse(hitCurrentPlan);
            }
            if(httpStatusException.getStatusCode()==404){
                return Jsoup.parse(notExist);
            }
            return Jsoup.parse(failedLoadPage);
        } catch (Exception e) {
            return Jsoup.parse(failedLoadPage);
        }
    };

    public PostStatus preHandleDataForScraper(Document document){
        if(document.html().contains(failedLoadPage)){
            System.out.println(failedLoadPage);
            return PostStatus.FAILED;
        }
        if(document.html().contains("You've hit the request limit for your current plan")){
            System.out.println("You've hit the request limit for your current plan");
            return PostStatus.FAILED;
        }
        return PostStatus.SUCCESS;
    };
}
