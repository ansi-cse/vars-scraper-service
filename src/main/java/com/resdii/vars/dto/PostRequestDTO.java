package com.resdii.vars.dto;

import com.resdii.ms.common.category.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
public class PostRequestDTO extends PostInfoDTO{
//    private MultipartFile thumbnail;
//    private List<MultipartFile> images;

    @Override
    public String toString () {
        return ToStringBuilder.reflectionToString(this);
    }
}
