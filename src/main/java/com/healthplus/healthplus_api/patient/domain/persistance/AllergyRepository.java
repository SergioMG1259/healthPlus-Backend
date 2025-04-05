package com.healthplus.healthplus_api.patient.domain.persistance;

import com.healthplus.healthplus_api.patient.domain.model.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long> {

}
