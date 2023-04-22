package com.resdii.vars.api;

import com.resdii.vars.config.FeignConfig;
import com.resdii.vars.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "postClient", url = "${post-server.url}", configuration = FeignConfig.class)
public interface PostApiClient {
    @RequestMapping(method = RequestMethod.POST, value = "/vland/system/posts",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResponseDTO<Map>> postNews(
            @RequestHeader(value = "Authorization", required = true) String authorizationHeader,
            @RequestBody Map<String, ?> data);
}
