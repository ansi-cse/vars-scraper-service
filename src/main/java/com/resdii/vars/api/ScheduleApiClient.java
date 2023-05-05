package com.resdii.vars.api;

import com.resdii.ms.common.rest.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ANSI.
 */

@FeignClient(value = "scheduleClient", url = "${microservice.vars-schedule-service.url}")
public interface ScheduleApiClient {
    @RequestMapping(method = RequestMethod.GET, value = "/schedule/post/notify/complete")
    ResponseEntity<ResponseDTO> notifyComplete(@RequestParam String jobType, @RequestParam String baseUrl,@RequestParam String prefix, @RequestParam String postType , @RequestParam String environment);
}
