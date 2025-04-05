package com.healthplus.healthplus_api.appointment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDateRequestDTO {

    @NotNull(message = "The date cannot be null")
    private LocalDateTime date;
}
