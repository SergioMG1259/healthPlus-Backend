package com.healthplus.healthplus_api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessTokenResponseDTO {

    private String accessToken;
}
