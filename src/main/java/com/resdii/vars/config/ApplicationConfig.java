package com.resdii.vars.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ms-config")
@Data
public class ApplicationConfig {

    @Value("${exampleValue:}")
    private String exampleValue;

    @Value("${exampleSecure:}")
    private String exampleSecure;
}
