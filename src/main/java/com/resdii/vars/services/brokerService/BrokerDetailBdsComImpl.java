package com.resdii.vars.services.brokerService;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * @author ANSI.
 */

@Service
public class BrokerDetailBdsComImpl implements BrokerDetail {

    @Override
    public String getName(Document document) {
        return document.select(".re__broker-name").text().trim();
    }

    @Override
    public String getEmail(Document document) {
        return document.select(".re__broker-address .re__icon-mail--sm").next().text();
    }

    @Override
    public String getPhone(Document document) {
        return document.select(".re__broker-address .re__icon-mobile--sm").next().text();
    }

    @Override
    public String getAddress(Document document) {
        return document.select(".re__broker-address .re__icon-location--sm").next().text();
    }

    @Override
    public String getDescription(Document document) {
        return document.select(".re__broker-detail-intro").html();
    }

    @Override
    public String getLogoUrl(Document document) {
        return document.select(".re__broker-avatar img").attr("src");
    }
}
