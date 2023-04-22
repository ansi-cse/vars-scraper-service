package com.resdii.vars.services.brokerService;

import org.jsoup.nodes.Document;

/**
 * @author ANSI.
 */
public class BrokerDetailMogiImpl implements BrokerDetail {
    @Override
    public String getName(Document document) {
        return document.select("#agent .title").text();
    }

    @Override
    public String getEmail(Document document) {
        return null;
    }

    @Override
    public String getPhone(Document document) {
        return document.select("#agent .agent-phone a").text().replace(" ","").trim();
    }

    @Override
    public String getAddress(Document document) {
        return null;
    }

    @Override
    public String getDescription(Document document) {
        return null;
    }

    @Override
    public String getLogoUrl(Document document) {
        return null;
    }
}
