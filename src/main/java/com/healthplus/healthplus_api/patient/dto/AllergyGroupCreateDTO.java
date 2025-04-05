package com.healthplus.healthplus_api.patient.dto;

import lombok.Data;

import java.util.List;

@Data
public class AllergyGroupCreateDTO {
    List<Long> allergiesId;
    List<AllergyRequestDTO> customAllergiesNames;
}
