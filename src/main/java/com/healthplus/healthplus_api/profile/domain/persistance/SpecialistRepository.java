package com.healthplus.healthplus_api.profile.domain.persistance;

import com.healthplus.healthplus_api.profile.domain.model.entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    boolean existsByNamesAndLastNames(String names, String lastNames);

    boolean existsByNamesAndLastNamesAndIdNot(String name, String lastName, Long id);
}
