package com.healthplus.healthplus_api.profile.dto;

import com.healthplus.healthplus_api.profile.domain.model.enums.Specialty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SpecialistResponseDTO {

    private String names;

    private String lastNames;

    private LocalDateTime birthDate;

    private String medicalInstitution;

    private Integer yearsOfExperience;

    @Enumerated(EnumType.STRING)
    private Specialty specialty;

    private String email;
}
