package com.healthplus.healthplus_api.profile.dto;

import com.healthplus.healthplus_api.profile.domain.model.enums.Specialty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SpecialistCreateDTO {

    @NotNull(message = "The names cannot be null")
    @NotBlank(message = "The names cannot be empty")
    @Size(max = 100, message = "Names cannot exceed 100 characters")
    private String names;

    @NotNull(message = "The last names cannot be null")
    @NotBlank(message = "The last names cannot be empty")
    @Size(max = 100, message = "Last names cannot exceed 100 characters")
    private String lastNames;

    @Past(message = "The birthday must be in the past")
    @NotNull(message = "The birthday cannot be null")
    private LocalDateTime birthDate;

    @NotNull(message = "The medical institution cannot be null")
    @NotBlank(message = "The medical institution cannot be empty")
    @Size(max = 150, message = "Medical Institution cannot exceed 150 characters")
    private String medicalInstitution;

    @NotNull(message = "The years of experience cannot be null")
    private Integer yearsOfExperience;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "The specialty cannot be null")
    private Specialty specialty;
}
