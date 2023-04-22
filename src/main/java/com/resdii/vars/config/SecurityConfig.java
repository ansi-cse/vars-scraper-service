package com.resdii.vars.config;

import com.resdii.ms.common.auth.NoodevSecurityConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig extends NoodevSecurityConfig {

    @Override
    public void init() {
        super.init();
        addExcludeRequest("/**");
    }
}
