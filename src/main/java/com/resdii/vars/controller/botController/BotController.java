package com.resdii.vars.controller.botController;

import com.resdii.ms.common.utils.RestUtils;
import com.resdii.vars.enums.BotStatus;
import com.resdii.vars.services.botService.BotServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/bot")
public class BotController {

    private BotServiceImpl botServiceImpl;

    @GetMapping("/start")
    public ResponseEntity startBot() {
        botServiceImpl.start();
        return RestUtils.responseOk(BotStatus.STARTED);
    }

    @GetMapping("/stop")
    public ResponseEntity stopBot() {
        botServiceImpl.stop();
        return RestUtils.responseOk(BotStatus.STOPPED);
    }

    @Autowired
    public void setBotService(BotServiceImpl botServiceImpl) {
        this.botServiceImpl = botServiceImpl;
    }
}
