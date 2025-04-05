package com.healthplus.healthplus_api.patient.domain.service;

import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import com.healthplus.healthplus_api.patient.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PatientService {
    List<PatientResponseDTO> findAll();
    Page<PatientResponseDTO> findAll(Pageable pageable);
    List<PatientResponseDTO> findBySpecialistId(Long specialistId);
    //Specification<Patient> filter(Long specialistId, String searchNameAndLastName, boolean female, boolean male,
    //                             Integer minAge, Integer maxAge);
    List<PatientResponseDTO> findBySpecialistIdFilter(Long specialistId, String searchByNameAndLastName, boolean female,
                                                      boolean male, Integer minAge, Integer maxAge, String sortBy);
    PatientDetailsDTO findById(Long patientId);
    PatientResponseDTO create(Long specialistId, PatientCreateDTO patientCreateDTO);
    PatientResponseDTO update(Long patientId, PatientUpdateDTO patientUpdateDTO);
    PatientResponseDTO updateNotes(Long patientId, NotesUpdateDTO notesUpdateDTO);
    MedicalInformationResponseDTO updateMedicalInformation(Long patientId, MedicalInformationUpdateDTO medicalInformationUpdateDTO);
    void delete(Long patientId);
}
