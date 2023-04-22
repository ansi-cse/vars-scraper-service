package com.resdii.vars.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectRequestDTO {
    @JsonProperty("input-title")
    private String projectName;
    @JsonProperty("input-description")
    private String description;
    @JsonProperty("input-investor")
    private int investor;
    @JsonProperty("input-lat")
    private String lat;
    @JsonProperty("input-lng")
    private String lng;
    @JsonProperty("input-address")
    private String address;
    @JsonProperty("input-province")
    private String province;
    @JsonProperty("input-district")
    private String district;
    @JsonProperty("input-ward")
    private String wards;
    @JsonProperty("input-area")
    private String square;
    @JsonProperty("input-status")
    private String status;
    @JsonProperty("input-apartment")
    private String numberOfApartment;
    @JsonProperty("input-price")
    private String price;
    @JsonProperty("input-price-max")
    private String maxPrice;
    @JsonProperty("input-price-min")
    private String minPrice;
    @JsonProperty("input-create-day")
    private String postDate;
    @JsonProperty("input-type")
    private int realEstateType;
    @JsonProperty("post_author_user")
    private CharSequence postAuthorUser;
    @JsonProperty("input-scale")
    private String scale;
    @JsonProperty("input-legal")
    private String legalDoc;
    @JsonProperty("input-progress")
    private String progress;
    @JsonProperty("input-utilities[]")
    private String[] utilities;
}
