package com.resdii.vars.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(value = "projectClient", url = "${microservice.vars-land-service.url}")
public interface ProjectApiClient {
    @RequestMapping(method = RequestMethod.POST, value = "/wp-json/v1/create_project", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> postProject(Map<String, ?> projectDTO);
}
