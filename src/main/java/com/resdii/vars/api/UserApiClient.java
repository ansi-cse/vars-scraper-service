package com.resdii.vars.api;

import com.resdii.vars.dto.ResponseDTO;
import com.resdii.vars.dto.UserInfoDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "userClient", url = "${microservice.vars-cms-service.url}")
public interface UserApiClient {
    @RequestMapping(method = RequestMethod.GET, value = "/users/me")
    @Headers("Content-Type: application/json")
    ResponseEntity<ResponseDTO<UserInfoDTO>> getUserInformation(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader);
}
