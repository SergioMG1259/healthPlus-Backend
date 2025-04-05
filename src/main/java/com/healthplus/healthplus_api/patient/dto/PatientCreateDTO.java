package com.healthplus.healthplus_api.patient.dto;

import com.healthplus.healthplus_api.patient.domain.model.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientCreateDTO {

    @NotNull(message = "The names cannot be null")
    @NotBlank(message = "The names cannot be empty")
    @Size(max = 100, message = "Names cannot exceed 100 characters")
    private String names;

    @NotNull(message = "The last names cannot be null")
    @NotBlank(message = "The last names cannot be empty")
    @Size(max = 100, message = "Last names cannot exceed 100 characters")
    private String lastNames;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "The gender cannot be null")
    private Gender gender;

    @Past(message = "The birthday must be in the past")
    @NotNull(message = "The birthday cannot be null")
    private LocalDateTime birthDate;

    @NotNull(message = "The dni cannot be null")
    @NotBlank(message = "The dni cannot be empty")
    @Pattern(regexp = "^[0-9]{8}$", message = "The DNI must have exactly 8 digits")
    @Size(max = 8)
    private String dni;

    @NotNull(message = "The phone number cannot be null")
    @Pattern(regexp = "^[0-9]{9}$", message = "The phone number must be valid, with 9 digits")
    @Size(max = 9)
    private String phoneNumber;

    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 150, message = "Address cannot exceed 150 characters")
    private String address;

    @Size(max = 250, message = "Notes cannot exceed 250 characters")
    private String notes;

    private AllergyGroupCreateDTO allergiesGroup;
}
