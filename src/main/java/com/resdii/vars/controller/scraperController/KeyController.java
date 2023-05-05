package com.resdii.vars.controller.scraperController;

import com.resdii.vars.dto.request.LoadKeyRequest;
import com.resdii.vars.helper.ApiKeyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @author ANSI.
 */

@RestController
@RequestMapping("/key")
public class KeyController {

    ApiKeyHelper apiKeyHelper;

    @RequestMapping(value="/load",method = RequestMethod.POST)
    public ResponseEntity<?> loadApiKey(@RequestBody LoadKeyRequest keyRequest) {
        apiKeyHelper.setApiKeys(keyRequest.getApiKeys().toArray(new String[0]));
        apiKeyHelper.setApiKeyForLoadPaging(keyRequest.getApiKeyForLoadPaging().toArray(new String[0]));
        apiKeyHelper.setApiKeyForCheckPaging(keyRequest.getApiKeyForCheckPaging());
        return ResponseEntity.ok().build();
    }

    @Autowired
    public void setApiKeyHelper(ApiKeyHelper apiKeyHelper) {
        this.apiKeyHelper = apiKeyHelper;
    }
}
