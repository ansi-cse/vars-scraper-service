package com.resdii.vars.services.scraperWebservice;

import com.resdii.vars.enums.PostStatus;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class ScraperServiceScrapingDogImpl implements ScraperService {

    @Override
    public Document loadPage(String url, String api_key) {
        return null;
    }
    @Override
    public PostStatus preHandleDataForScraper(Document document) {
        return PostStatus.SUCCESS;
    }
}
