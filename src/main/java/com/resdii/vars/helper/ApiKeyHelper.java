package com.resdii.vars.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ApiKeyHelper {
    private String[] apiKeys;

    private String[] apiKeyForLoadPaging;

    private String apiKeyForCheckPaging;

    public String[] getApiKeyForLoadPaging() {return apiKeyForLoadPaging;}

    public String[] getApiKeys() {
        return apiKeys;
    }

    public String getApiKeyForCheckPaging() {return apiKeyForCheckPaging;}

    public void setApiKeys(String[] apiKeys) {
        this.apiKeys = apiKeys;
    }

    public void setApiKeyForLoadPaging(String[] apiKeyForLoadPaging) {
        this.apiKeyForLoadPaging = apiKeyForLoadPaging;
    }

    public void setApiKeyForCheckPaging(String apiKeyForCheckPaging) {
        this.apiKeyForCheckPaging = apiKeyForCheckPaging;
    }
}
