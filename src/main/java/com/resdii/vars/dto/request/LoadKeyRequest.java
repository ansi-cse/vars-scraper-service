package com.resdii.vars.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ANSI.
 */
@Data
@NoArgsConstructor
public class LoadKeyRequest {
    List<String> apiKeys;
    List<String> apiKeyForLoadPaging;
    String apiKeyForCheckPaging;
}
