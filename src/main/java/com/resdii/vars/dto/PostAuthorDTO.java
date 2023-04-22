package com.resdii.vars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostAuthorDTO {
    private String name;
    private String phone;
    private String email;
    private String address;
}
