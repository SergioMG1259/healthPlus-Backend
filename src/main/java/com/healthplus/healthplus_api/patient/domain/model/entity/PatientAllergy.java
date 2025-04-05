package com.healthplus.healthplus_api.patient.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class PatientAllergy implements Serializable {
    @EmbeddedId
    private PatientAllergyPK id = new PatientAllergyPK();

    @JsonIgnore
    @ManyToOne
    @MapsId("patientId")
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @MapsId("allergyId")
    @JoinColumn(name = "allergy_id")
    private Allergy allergy;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
