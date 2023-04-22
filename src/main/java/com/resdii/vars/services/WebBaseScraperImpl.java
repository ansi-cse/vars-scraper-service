package com.resdii.vars.services;

import com.resdii.vars.helper.ApiKeyHelper;
import com.resdii.vars.services.WebBaseScraper;
import com.resdii.vars.services.scraperWebservice.ScraperService;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class WebBaseScraperImpl implements WebBaseScraper {

    @Value("${rotating-proxy.user-name}")
    String authUser;
    @Value("${rotating-proxy.password}")
    String authPassword;
    @Value("${rotating-proxy.host}")
    String host;
    @Value("${rotating-proxy.port}")
    Integer port;

    protected ApiKeyHelper apiKeyHelper;
    protected ScraperService scraperService;

    @SneakyThrows
    public Document loadPage(String url, String api_key){
        Document document= scraperService.loadPage(url, api_key);
        return document;
    };
    public Elements parsePage(Document document){
        return document.select("html");
    };

    public void setScraperWebService(ScraperService scraperService) {
        this.scraperService = scraperService;
    }
    @Autowired
    public void setApiKeyHelper(ApiKeyHelper apiKeyHelper) {
        this.apiKeyHelper = apiKeyHelper;
    }
}
