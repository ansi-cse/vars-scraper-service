package com.resdii.vars.api;

import com.resdii.vars.dto.CategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "CategoryClient", url = "${microservice.vars-land-service.url}")
public interface CategoryApiClient {
    @RequestMapping(method = RequestMethod.GET, value = "/wp-json/v1/get-categories", consumes = "application/json")
    ResponseEntity<List<CategoryDTO>> getCategories(@RequestParam Map<String, String> categoryRequest);

    @RequestMapping(method = RequestMethod.GET, value = "/wp-json/v1/get-investors", consumes = "application/json")
    ResponseEntity<List<CategoryDTO>> getListInvestor();
}
