package com.resdii.vars.api;

import com.resdii.vars.dto.request.InvestorRequestDTO;
import com.resdii.vars.dto.response.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(value = "investorClient", url = "${microservice.vars-land-service.url}")
public interface InvestorApiClient {
    @RequestMapping(method = RequestMethod.POST, value = "/wp-json/v1/create_investor", consumes = "application/json")
    ResponseEntity<ResponseDTO<Map>> investor(@RequestBody InvestorRequestDTO inputTitle);
}
