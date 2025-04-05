package com.healthplus.healthplus_api.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NotesUpdateDTO {

    @NotNull(message = "The notes cannot be null")
    @NotBlank(message = "The notes cannot be empty")
    @Size(max = 250, message = "Notes cannot exceed 250 characters")
    private String notes;
}
