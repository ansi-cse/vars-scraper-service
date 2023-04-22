package com.resdii.vars.services.brokerService.brokerPageFactory;

import com.resdii.vars.services.brokerService.BrokerPage;
import com.resdii.vars.services.brokerService.BrokerPageBdsComImpl;
import com.resdii.vars.services.brokerService.BrokerPageMogiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author ANSI.
 */

@Component
@Scope("singleton")
public class BrokerPageFactory {

    BrokerPageBdsComImpl brokerPageBdsComImpl;

    BrokerPageMogiImpl brokerPageMogiImpl;

    public BrokerPage getBrokerPage(String baseUrl){
        BrokerPage brokerPage=null;
        switch (baseUrl) {
            case "https://batdongsan.com.vn/":
                brokerPage = brokerPageBdsComImpl;
                break;
            case "https://mogi.vn/":
                brokerPage = brokerPageMogiImpl;
                break;
        }
        return brokerPage;
    }

    @Autowired
    public void setBdsComBrokerPage(BrokerPageBdsComImpl brokerPageBdsComImpl) {
        this.brokerPageBdsComImpl = brokerPageBdsComImpl;
    }

    @Autowired
    public void setMogiBrokerPage(BrokerPageMogiImpl brokerPageMogiImpl) {
        this.brokerPageMogiImpl = brokerPageMogiImpl;
    }
}
