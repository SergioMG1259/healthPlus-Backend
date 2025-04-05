package com.healthplus.healthplus_api.patient.dto;

import com.healthplus.healthplus_api.patient.domain.model.entity.Allergy;
import com.healthplus.healthplus_api.patient.domain.model.entity.CustomAllergy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AllergyGroupResponseDTO {
    List<Allergy> allergies;
    List<CustomAllergy> customAllergies;
}
