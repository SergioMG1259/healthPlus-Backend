package com.healthplus.healthplus_api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    @NotNull(message = "The current password cannot be null")
    @NotBlank(message = "The current password cannot be empty")
    @Size(max = 30, message = "Current password cannot exceed 30 characters")
    private String currentPassword;

    @NotNull(message = "The new password cannot be null")
    @NotBlank(message = "The new password cannot be empty")
    @Size(max = 30, message = "New password cannot exceed 30 characters")
    private String newPassword;
}
