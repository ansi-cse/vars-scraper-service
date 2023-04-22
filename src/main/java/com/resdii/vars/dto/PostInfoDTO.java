package com.resdii.vars.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.resdii.ms.common.category.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
public class PostInfoDTO {
    private BigInteger id;
    private String code;
    private String title;
    private String description;
    private Category status;
    private Category reStatus;
    private Category typeRealEstate;
    private Category postType;
    private String address;
    private Double lat;
    private Double lng;
    private Category province;
    private Category district;
    private Category wards;
    private String street;
    private Float square;
    private Double price;
    private Category legalDoc;
    private Category direction;
    private Category entrance;
    private Float frontWidth;
    private Integer floor;
    private Integer bedroom;
    private Integer bathroom;
    private List<String> furniture;
    private Category priority;
    private PostAuthorDTO author;
    private String postDate;
    private String postDateDuration;
    private String startDate;
    private String endDate;
    private String thumbnailUrl;
    private List<String> imageUrls;
    private Category postBy;
    private BigInteger walletId;

    public PostInfoDTO(BigInteger id){
        this.id = id;
    }
    @Override
    public String toString () {
        return ToStringBuilder.reflectionToString(this);
    }
}
