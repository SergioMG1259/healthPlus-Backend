package com.healthplus.healthplus_api.patient.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class MedicalInformation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal height;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal BMI;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal cholesterol;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal bloodSugar;

    @Column(nullable = false)
    private Integer systolicPressure;

    @Column(nullable = false)
    private Integer diastolicPressure;

    @Column(nullable = false)
    private Integer heartRate;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name="FK_medical_information_patient"))
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
