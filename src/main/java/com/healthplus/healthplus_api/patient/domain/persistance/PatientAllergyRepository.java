package com.healthplus.healthplus_api.patient.domain.persistance;

import com.healthplus.healthplus_api.patient.domain.model.entity.PatientAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientAllergyRepository extends JpaRepository<PatientAllergy, PatientAllergy> {
    List<PatientAllergy> findByPatientId(Long patientId);
}
