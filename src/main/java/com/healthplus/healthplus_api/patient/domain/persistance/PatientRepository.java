package com.healthplus.healthplus_api.patient.domain.persistance;

import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    boolean existsByDniAndSpecialistId(String dni, Long specialistId);

    boolean existsByEmailAndSpecialistId(String email, Long specialistId);

    boolean existsByNamesAndLastNamesAndSpecialistId(String name, String lastName, Long specialistId);

    boolean existsByDniAndIdNotAndSpecialistId(String dni, Long id, Long specialistId);

    boolean existsByEmailAndIdNotAndSpecialistId(String email, Long id, Long specialistId);

    boolean existsByNamesAndLastNamesAndIdNotAndSpecialistId(String name, String lastName, Long id ,Long specialistId);

    @Query("SELECT a FROM Patient a " +
            "WHERE a.specialist.id = :specialistId " +
            "ORDER BY a.createdAt DESC")
    List<Patient> findBySpecialistId(@Param("specialistId") Long specialistId);

}
