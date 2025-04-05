package com.healthplus.healthplus_api.patient.domain.service;

import com.healthplus.healthplus_api.patient.domain.model.entity.Allergy;
import com.healthplus.healthplus_api.patient.dto.AllergyGroupCreateDTO;
import com.healthplus.healthplus_api.patient.dto.AllergyGroupResponseDTO;
import com.healthplus.healthplus_api.patient.dto.AllergyRequestDTO;

import java.util.List;

public interface AllergyService {
    List<Allergy> findAll();
    Allergy create(AllergyRequestDTO allergyRequestDTO);
    Allergy update(Long allergyId, AllergyRequestDTO allergyRequestDTO);
    void delete(Long allergyId);
    //List<Allergy> updatePatientAllergies(Long patientId, PatientAllergyCreateDTO patientAllergyCreateDTO);
    //List<CustomAllergy> updatePatientCustomAllergies(Long patientId, CustomAllergiesCreateDTO customAllergiesCreateDTO);
    AllergyGroupResponseDTO updateAllergiesInPatient(Long patientId, AllergyGroupCreateDTO allergyGroupCreateDTO);
    List<Allergy> findPatientAllergiesByPatientId(Long patientId);
}
