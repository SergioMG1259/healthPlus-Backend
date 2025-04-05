package com.healthplus.healthplus_api.appointment.dto;

import com.healthplus.healthplus_api.appointment.domain.model.enums.MedicalIssue;
import com.healthplus.healthplus_api.patient.dto.PatientShortResponseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentResponseDTO {

    private Long id;

    private BigDecimal price;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private MedicalIssue issue;

    private PatientShortResponseDTO patient;
}
