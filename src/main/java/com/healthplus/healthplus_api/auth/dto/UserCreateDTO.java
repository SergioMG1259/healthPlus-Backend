package com.healthplus.healthplus_api.auth.dto;

import com.healthplus.healthplus_api.profile.dto.AdminCreateDTO;
import com.healthplus.healthplus_api.profile.dto.SpecialistCreateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {

    @NotNull(message = "The email cannot be null")
    @NotBlank(message = "The email cannot be empty")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @NotNull(message = "The password cannot be null")
    @NotBlank(message = "The password cannot be empty")
    @Size(max = 30, message = "Password cannot exceed 30 characters")
    private String password;

    private SpecialistCreateDTO specialistCreateDTO;

    private AdminCreateDTO adminCreateDTO;
}
