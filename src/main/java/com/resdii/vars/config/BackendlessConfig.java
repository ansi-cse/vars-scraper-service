package com.resdii.vars.config;

import com.resdii.noodev.logs.AppLogService;
import com.resdii.noodev.sdk.Noo;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "noodev.backendless")
@Data
public class BackendlessConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendlessConfig.class);

    @Value("${url:}")
    private String url;

    @Value("${appId:}")
    private String appId;

    @Value("${javaKey:}")
    private String javaKey;

    @PostConstruct
    public void init() {
        AppLogService.info(LOGGER, "Init Noodev Backendless SDK");
    }
}
