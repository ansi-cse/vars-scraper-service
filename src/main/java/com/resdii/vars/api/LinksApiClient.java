package com.resdii.vars.api;

import com.resdii.vars.dto.PagingDTO;
import com.resdii.vars.dto.PagingItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "linksClient", url = "https://muaban.net")
public interface LinksApiClient {
    @RequestMapping(method = RequestMethod.GET, value = "/listing/v1/classifieds/listing")
    ResponseEntity<PagingDTO<PagingItemDTO>> getLinksByPageIndex(
            @RequestParam int subcategory_id,
            @RequestParam int category_id,
            @RequestParam int limit,
            @RequestParam int offset);
}
