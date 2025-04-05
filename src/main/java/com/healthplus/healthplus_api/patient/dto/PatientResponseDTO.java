package com.healthplus.healthplus_api.patient.dto;

import com.healthplus.healthplus_api.patient.domain.model.enums.Gender;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientResponseDTO {

    private Long id;

    private String names;

    private String lastNames;

    private Gender gender;

    private LocalDateTime birthDate;

    private String dni;

    private String phoneNumber;

    private String email;

    private String address;

    private String notes;

    private LocalDateTime createdAt;
}
