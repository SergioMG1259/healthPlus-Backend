package com.healthplus.healthplus_api.profile.domain.persistance;

import com.healthplus.healthplus_api.profile.domain.model.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
