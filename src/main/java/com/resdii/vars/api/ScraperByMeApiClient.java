package com.resdii.vars.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "scraperByMeClient", url = "http://localhost:8000")
public interface ScraperByMeApiClient {

    @RequestMapping(method = RequestMethod.GET, value = "/scraper")
    ResponseEntity<String> getHtml(@RequestParam String url);
    @RequestMapping(method = RequestMethod.GET, value = "/scraper/nhatot")
    ResponseEntity<String> getHtmlForNhaTot(@RequestParam String url);
    @RequestMapping(method = RequestMethod.GET, value = "/scraper/bdscom")
    ResponseEntity<String> getHtmlForBdsCom(@RequestParam String url);
    @RequestMapping(method = RequestMethod.GET, value = "/scraper/bdscom/investor")
    ResponseEntity<String> getHtmlForBdsComInvestor(@RequestParam String url);
    @RequestMapping(method = RequestMethod.GET, value = "/scraper/bdscom/enterpriseBroker")
    ResponseEntity<String> getHtmlForBdsComEnterpriseBrokerPage(@RequestParam String url);
    @RequestMapping(method = RequestMethod.GET, value = "/scraper/bdscom/personalBroker")
    ResponseEntity<String> getHtmlForBdsComPersonalBrokerPage(@RequestParam String url);
}
