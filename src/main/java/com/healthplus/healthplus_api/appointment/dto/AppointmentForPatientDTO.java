package com.healthplus.healthplus_api.appointment.dto;

import com.healthplus.healthplus_api.appointment.domain.model.enums.MedicalIssue;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentForPatientDTO {

    private Long id;

    private BigDecimal price;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private MedicalIssue issue;

    private LocalDateTime createdDate;
}
