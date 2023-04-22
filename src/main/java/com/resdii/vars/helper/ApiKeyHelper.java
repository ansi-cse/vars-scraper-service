package com.resdii.vars.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ApiKeyHelper {
    @Value("${scraper-api.keys}")
    private String[] api_keys;

    @Value("${scraper-api.api_key_for_load_paging}")
    private String[] api_key_for_load_paging;

    @Value("${scraper-api.api_key_for_check_paging}")
    private String api_key_for_check_paging;

    public String[] getApi_key_for_load_paging() {return api_key_for_load_paging;}

    public String[] getApi_keys() {
        return api_keys;
    }

    public String getApi_key_for_check_paging() {return api_key_for_check_paging;}
}
