package com.healthplus.healthplus_api.patient.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicalInformationUpdateDTO {

    @Digits(integer = 1, fraction = 2, message = "Height must have at most 3 digits before and 2 after the decimal point")
    @NotNull(message = "The height cannot be null")
    private BigDecimal height;

    @Digits(integer = 3, fraction = 2, message = "Weight must have at most 5 digits before and 2 after the decimal point")
    @NotNull(message = "The weight cannot be null")
    private BigDecimal weight;

    @Digits(integer = 3, fraction = 2, message = "Cholesterol must have at most 5 digits before and 2 after the decimal point")
    @NotNull(message = "The cholesterol cannot be null")
    private BigDecimal cholesterol;

    @Digits(integer = 3, fraction = 2, message = "Blood sugar must have at most 5 digits before and 2 after the decimal point")
    @NotNull(message = "The blood sugar cannot be null")
    private BigDecimal bloodSugar;

    @NotNull(message = "The systolic pressure cannot be null")
    private Integer systolicPressure;

    @NotNull(message = "The diastolic pressure cannot be null")
    private Integer diastolicPressure;

    @NotNull(message = "The hearth rate cannot be null")
    @Max(value = 999, message = "The heart rate cannot exceed 999")
    private Integer heartRate;
}
