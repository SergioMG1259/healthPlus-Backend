package com.healthplus.healthplus_api.patient.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class CustomAllergy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100)
    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_custom_allergy_patient"))
    private Patient patient;
}