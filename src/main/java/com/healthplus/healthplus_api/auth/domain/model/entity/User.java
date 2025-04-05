package com.healthplus.healthplus_api.auth.domain.model.entity;

import com.healthplus.healthplus_api.auth.domain.model.enums.Role;
import com.healthplus.healthplus_api.profile.domain.model.entity.Admin;
import com.healthplus.healthplus_api.profile.domain.model.entity.Specialist;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 150)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Specialist specialist;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Admin admin;
}
