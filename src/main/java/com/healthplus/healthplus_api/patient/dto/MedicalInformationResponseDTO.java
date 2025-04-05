package com.healthplus.healthplus_api.patient.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicalInformationResponseDTO {

    private BigDecimal height;

    private BigDecimal weight;

    private BigDecimal BMI;

    private BigDecimal cholesterol;

    private BigDecimal bloodSugar;

    private Integer systolicPressure;

    private Integer diastolicPressure;

    private Integer heartRate;
}
