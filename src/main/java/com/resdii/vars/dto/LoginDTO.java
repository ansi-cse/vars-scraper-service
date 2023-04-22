package com.resdii.vars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDTO {
    private String clientId;
    private String clientSecret;
    private String phone;
    private String password;

    public LoginDTO(String clientId, String clientSecret, String phone, String password) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.phone = phone;
        this.password = password;
    }
}
