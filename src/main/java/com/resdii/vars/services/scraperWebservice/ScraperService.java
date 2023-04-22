package com.resdii.vars.services.scraperWebservice;

import com.resdii.vars.enums.PostStatus;
import org.jsoup.nodes.Document;

// Template
public interface ScraperService {
    Document loadPage(String url, String api_key);
    PostStatus preHandleDataForScraper(Document document);
}
