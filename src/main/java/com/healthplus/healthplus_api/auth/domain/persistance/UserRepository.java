package com.healthplus.healthplus_api.auth.domain.persistance;

import com.healthplus.healthplus_api.auth.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    Optional<User> findByEmail(String email);
}
