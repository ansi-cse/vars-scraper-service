package com.resdii.vars.dto;

import com.resdii.vars.dto.PostRequestDTO;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class PostDocument extends PostRequestDTO {
    String rawLegalDoc;
    String rawRealEstate;
    String rawEntrance;
    String rawAddress;
    String rawPriceText;
    String[] imagesUrlInDisk;
    String thumbnailUrlInDisk;
    String rawHtml;
    String url;
    String[] imageWithoutWaterMaskUrl;
    String thumbnailWithoutWaterMaskUrl;
}
