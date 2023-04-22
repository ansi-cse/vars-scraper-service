package com.resdii.vars.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "districtClient", url = "https://alonhadat.com.vn")
public interface DistrictApiClient {
    @RequestMapping(method = RequestMethod.GET, value = "/handler/Handler.ashx")
    ResponseEntity<String> getLinksDistrict(
            @RequestParam int command, @RequestParam int matinh);
}
