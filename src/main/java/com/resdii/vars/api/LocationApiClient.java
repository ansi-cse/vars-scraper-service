package com.resdii.vars.api;

import com.resdii.vars.config.FeignConfig;
import com.resdii.vars.dto.AutoCompleteDTO;
import com.resdii.vars.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@FeignClient(value = "locationClient", url = "${post-server.url}", configuration = FeignConfig.class)
public interface LocationApiClient {
    @RequestMapping(method = RequestMethod.GET, value = "/vmap/places/autocomplete")
    ResponseEntity<ResponseDTO<ArrayList<AutoCompleteDTO>>> autoComplete(
            @RequestParam String keyword);
}
