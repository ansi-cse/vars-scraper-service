package com.resdii.vars.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvestorRequestDTO {
    @JsonProperty("input-title")
    String inputTitle;
}