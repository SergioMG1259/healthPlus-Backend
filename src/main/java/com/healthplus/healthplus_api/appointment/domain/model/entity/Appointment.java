package com.healthplus.healthplus_api.appointment.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthplus.healthplus_api.appointment.domain.model.enums.MedicalIssue;
import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Appointment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicalIssue issue;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_appointment_patient"))
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
