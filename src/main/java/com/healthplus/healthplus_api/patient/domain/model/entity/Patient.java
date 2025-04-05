package com.healthplus.healthplus_api.patient.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthplus.healthplus_api.appointment.domain.model.entity.Appointment;
import com.healthplus.healthplus_api.patient.domain.model.enums.Gender;
import com.healthplus.healthplus_api.profile.domain.model.entity.Specialist;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100)
    private String names;

    @Column(nullable = false)
    @Size(max = 100)
    private String lastNames;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDateTime birthDate;

    @Column(nullable = false)
    @Size(max = 8)
    private String dni;

    @Column(nullable = false)
    @Size(max = 9)
    private String phoneNumber;

    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 150)
    private String address;

    @Size(max = 250)
    private String notes;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // sin esto entra a error porque se forma un ciclo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="specialist_id",referencedColumnName ="id",
            foreignKey = @ForeignKey(name="FK_patient_specialist"))
    private Specialist specialist;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private MedicalInformation medicalInformation;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<PatientAllergy> allergies;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<CustomAllergy> customAllergies;

    @OneToMany (mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
