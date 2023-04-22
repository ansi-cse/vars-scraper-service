package com.resdii.vars.services;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

// Template
public interface WebBaseScraper {
    Document loadPage(String url, String api_key);
    Elements parsePage(Document document);
}
