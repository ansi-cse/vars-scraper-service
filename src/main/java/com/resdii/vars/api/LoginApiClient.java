package com.resdii.vars.api;

import com.resdii.vars.dto.LoginDTO;
import com.resdii.vars.dto.LoginResponseDTO;
import com.resdii.vars.dto.ResponseDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "loginClient", url = "${microservice.vars-id-service.url}")
public interface LoginApiClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/auth/phone")
    @Headers("Content-Type: application/json")
    ResponseEntity<ResponseDTO<LoginResponseDTO>> login(@RequestBody LoginDTO loginDTO);

    @RequestMapping(method = RequestMethod.GET, value = "vcms/users/me")
    @Headers("Content-Type: application/json")
    ResponseEntity<ResponseDTO<LoginResponseDTO>> getUserInformation(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader);
}
