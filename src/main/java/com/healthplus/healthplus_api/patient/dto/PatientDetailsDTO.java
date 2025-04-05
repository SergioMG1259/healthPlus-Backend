package com.healthplus.healthplus_api.patient.dto;

import com.healthplus.healthplus_api.appointment.dto.AppointmentForPatientDTO;
import com.healthplus.healthplus_api.patient.domain.model.entity.Allergy;
import com.healthplus.healthplus_api.patient.domain.model.entity.CustomAllergy;
import com.healthplus.healthplus_api.patient.domain.model.enums.Gender;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PatientDetailsDTO {
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

    private MedicalInformationResponseDTO medicalInformation;

    private List<PatientAllergyResponseDTO> allergies;

    private List<CustomAllergy> customAllergies;

    private List<AppointmentForPatientDTO> appointments;
}

@Data
class PatientAllergyResponseDTO {
    private Allergy allergy;
}
