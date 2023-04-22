package com.resdii.vars.config;

import com.resdii.ms.common.config.AbstractFeignConfig;
import feign.Logger;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FeignConfig extends AbstractFeignConfig {
}