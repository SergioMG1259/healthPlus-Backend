package com.healthplus.healthplus_api.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AllergyRequestDTO {
    @NotBlank(message = "Allergy cannot be empty")
    @NotNull(message = "Allergy cannot be null")
    @Size(max = 100, message = "Allergy cannot exceed 100 characters")
    private String name;
}
