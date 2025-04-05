package com.healthplus.healthplus_api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {

    @NotNull(message = "The email cannot be null")
    @NotBlank(message = "The email cannot be empty")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @NotNull(message = "The email cannot be null")
    @NotBlank(message = "The email cannot be empty")
    @Size(max = 30, message = "Password cannot exceed 30 characters")
    private String password;
}
