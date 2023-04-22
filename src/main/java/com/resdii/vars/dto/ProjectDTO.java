package com.resdii.vars.dto;

import com.resdii.vars.enums.ProjectStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ProjectDTO {
    String _id;
    String _class;
    String url;
    String projectName;
    String projectType;
    String address;
    String description;
    String price;
    String status;
    String extraStatus;
    String square;
    String numOfApartments;
    List<String> facilities;
    Map<String, String> detailInformation;
    String groundDrawingUrl;
    LocationDTO location;
    String investor;
    String rawHtml;

    String progress;
    List<String> progressImages;

    String scale;
    String legalDoc;

    String numOfHouse;

    List<String> listImage;
    List<String> listVideo;
    List<String> listImageInDetail;
    List<String> listImageDetailGroundDrawing;

    String province;
    String district;
    String wards;

    String maxPrice;
    String minPrice;
    String priceUnit;

    String investorUrl;

    boolean loadImageFailed;

    boolean isPostToServer;
}
