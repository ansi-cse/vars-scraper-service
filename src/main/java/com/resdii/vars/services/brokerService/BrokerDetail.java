package com.resdii.vars.services.brokerService;

import org.jsoup.nodes.Document;

/**
 * @author ANSI.
 */
public interface BrokerDetail {
    String getName(Document document);
    String getEmail(Document document);
    String getPhone(Document document);
    String getAddress(Document document);
    String getDescription(Document document);
    String getLogoUrl(Document document);
}
