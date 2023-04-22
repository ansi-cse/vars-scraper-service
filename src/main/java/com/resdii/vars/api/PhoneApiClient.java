package com.resdii.vars.api;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient( value = "phoneClient", url = "http://localhost:8000/")
public interface PhoneApiClient {
    @RequestMapping(method = RequestMethod.POST, value = "scraper", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Headers({"User-Agent: Mozilla/4.0"})
    ResponseEntity<String> decryptPhone(Map<String, ?> data);
}
