package com.resdii.vars.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * @author ANSI.
 */

@FeignClient(value = "RemoverClient", url = "${microservice.remover-service.url}")
public interface GetImageWithoutWaterMaskUrlApiClient {
    @RequestMapping(method = RequestMethod.GET, value = "/images/removewatermask")
    ResponseEntity<Map<String, String>> getLinks(@RequestParam String url);
}
