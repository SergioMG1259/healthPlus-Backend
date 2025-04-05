package com.healthplus.healthplus_api.patient.domain.persistance;

import com.healthplus.healthplus_api.patient.domain.model.entity.CustomAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomAllergyRepository extends JpaRepository<CustomAllergy,Long> {
    List<CustomAllergy> findByPatientId(Long patientId);
}
