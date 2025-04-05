package com.healthplus.healthplus_api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private Long userRoleId;
    private String role;
    private String accessToken;
}
