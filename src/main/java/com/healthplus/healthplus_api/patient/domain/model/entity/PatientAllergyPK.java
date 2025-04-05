package com.healthplus.healthplus_api.patient.domain.model.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class PatientAllergyPK implements Serializable {
    private Long patientId;
    private Long allergyId;
}
