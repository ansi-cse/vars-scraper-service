package com.resdii.vars.controller.investorController;

import com.resdii.ms.common.utils.RestUtils;
import com.resdii.vars.enums.BotStatus;
import com.resdii.vars.services.investorService.InvestorServiceImpl;
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
@RequestMapping("/investor")
public class InvestorController {
    InvestorServiceImpl investorServiceImpl;

    @GetMapping("/insert")
    public ResponseEntity insertInvestor(@RequestParam String projectTable) {
        investorServiceImpl.insertInvestor(projectTable);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/extract")
    public ResponseEntity extractInvestor(@RequestParam String projectTable) {
        investorServiceImpl.extractInvestor(projectTable);
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @Autowired
    public void setInvestorService(InvestorServiceImpl investorServiceImpl) {
        this.investorServiceImpl = investorServiceImpl;
    }
}
