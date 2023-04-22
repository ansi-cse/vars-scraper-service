package com.resdii.vars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDTO {
    String accessToken;
    String refreshToken;
}
