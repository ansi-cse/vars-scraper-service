package com.resdii.vars.controller.brokerController;

import com.resdii.ms.common.utils.RestUtils;
import com.resdii.vars.enums.BotStatus;
import com.resdii.vars.services.brokerService.BrokerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ANSI.
 */

@RestController
@RequestMapping("/broker")
public class BrokerController {
    BrokerServiceImpl brokerServiceImpl;

    @GetMapping("/getLinks")
    public ResponseEntity getLinks(@RequestParam String baseUrl) {
        brokerServiceImpl.getLinks(baseUrl);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/getBrokerDetail")
    public ResponseEntity getBrokerDetail(@RequestParam String baseUrl) {
        brokerServiceImpl.getDetail(baseUrl);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/updateBrokerDetail")
    public ResponseEntity updateBrokerDetail(@RequestParam String baseUrl) {
        brokerServiceImpl.update(baseUrl);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @Autowired
    public void setBrokerService(BrokerServiceImpl brokerServiceImpl) {
        this.brokerServiceImpl = brokerServiceImpl;
    }
}
