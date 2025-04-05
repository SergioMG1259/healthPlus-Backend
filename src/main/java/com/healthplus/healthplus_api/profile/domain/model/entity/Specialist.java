package com.healthplus.healthplus_api.profile.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import com.healthplus.healthplus_api.auth.domain.model.entity.User;
import com.healthplus.healthplus_api.profile.domain.model.enums.Specialty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Specialist implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100)
    private String names;

    @Column(nullable = false)
    @Size(max = 100)
    private String lastNames;

    @Column(nullable = false)
    private LocalDateTime birthDate;

    @Column(nullable = false)
    @Size(max = 150)
    private String medicalInstitution;

    @Column(nullable = false)
    private Integer yearsOfExperience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialty specialty;

    @JsonIgnore
    @OneToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name="FK_specialist_user"))
    private User user;

    @JsonIgnore // para que el json no regrese la lista de pacientes de cada especialista
    @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL)
    private List<Patient> patientList;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
