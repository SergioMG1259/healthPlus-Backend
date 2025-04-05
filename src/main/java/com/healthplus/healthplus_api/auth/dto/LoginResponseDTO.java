package com.healthplus.healthplus_api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private Long userRoleId;
    private String role;
    private String accessToken;
    private String refreshToken;
}
