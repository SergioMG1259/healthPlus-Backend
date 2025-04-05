package com.healthplus.healthplus_api.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminCreateDTO {

    @NotNull(message = "The names cannot be null")
    @NotBlank(message = "The names cannot be empty")
    @Size(max = 100, message = "Names cannot exceed 100 characters")
    private String names;

    @NotNull(message = "The last names cannot be null")
    @NotBlank(message = "The last names cannot be empty")
    @Size(max = 100, message = "Last names cannot exceed 100 characters")
    private String lastNames;
}
