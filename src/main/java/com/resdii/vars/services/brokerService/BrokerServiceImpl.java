package com.resdii.vars.services.brokerService;

import com.resdii.vars.enums.BrokerType;
import com.resdii.vars.services.brokerService.brokerPageFactory.BrokerPageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ANSI.
 */

@Service
public class BrokerServiceImpl implements BrokerService{

    BrokerPageFactory brokerPageFactory;

    public void getLinks(String baseUrl){
       BrokerPage brokerPage= brokerPageFactory.getBrokerPage(baseUrl);
       brokerPage.getLinks();
    };

    public void getDetail(String baseUrl){
        BrokerPage brokerPage= brokerPageFactory.getBrokerPage(baseUrl);
        brokerPage.getDetail(BrokerType.ETP.value);
        brokerPage.getDetail(BrokerType.PER.value);
    };

    public void update(String baseUrl){
//        BrokerPage brokerPage= brokerPageFactory.getBrokerPage(baseUrl);
//        brokerPage.update(BrokerType.PER.value);
    };

    @Autowired
    public void setBrokerPageFactory(BrokerPageFactory brokerPageFactory) {
        this.brokerPageFactory = brokerPageFactory;
    }
}
