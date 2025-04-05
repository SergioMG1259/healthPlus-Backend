package com.healthplus.healthplus_api.appointment.dto;

import com.healthplus.healthplus_api.appointment.domain.model.enums.MedicalIssue;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentCreateDTO {

    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits before and 2 after the decimal point")
    @NotNull(message = "The price cannot be null")
    private BigDecimal price;

    @NotNull(message = "The start date cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "The end date cannot be null")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "The issue cannot be null")
    private MedicalIssue issue;
}
