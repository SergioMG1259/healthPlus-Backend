package com.healthplus.healthplus_api.patient.api;

import com.healthplus.healthplus_api.patient.domain.service.AllergyService;
import com.healthplus.healthplus_api.patient.domain.service.PatientService;
import com.healthplus.healthplus_api.patient.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@PreAuthorize("hasAnyRole('ADMIN', 'SPECIALIST')")
public class PatientController {

    @Autowired
    private PatientService patientService;
    @Autowired
    private AllergyService allergyService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PatientResponseDTO>> findAllPatients() {

        List<PatientResponseDTO> patients = this.patientService.findAll();
        return new ResponseEntity<List<PatientResponseDTO>>(patients, HttpStatus.OK);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDetailsDTO> findPatientById(@PathVariable Long patientId) {

        PatientDetailsDTO patient = this.patientService.findById(patientId);
        return new ResponseEntity<PatientDetailsDTO>(patient, HttpStatus.OK);
    }

    @GetMapping("/specialist/{specialistId}")
    public ResponseEntity<List<PatientResponseDTO>> findBySpecialistId(@PathVariable Long specialistId) {

        List<PatientResponseDTO> patients = this.patientService.findBySpecialistId(specialistId);
        return new ResponseEntity<List<PatientResponseDTO>>(patients, HttpStatus.OK);
    }

    @GetMapping("/filter/specialist/{specialistId}")
    public ResponseEntity<List<PatientResponseDTO>> findBySpecialistIdFilter(@PathVariable Long specialistId,
            @RequestParam(required = false) String searchByNameAndLastName,
            @RequestParam(required = false) Boolean female,
            @RequestParam(required = false) Boolean male,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false, defaultValue = "default") String sortBy
    ) {
        List<PatientResponseDTO> patients = patientService.findBySpecialistIdFilter(specialistId,searchByNameAndLastName, female,
                                                                        male, minAge, maxAge, sortBy);
        return new ResponseEntity<List<PatientResponseDTO>>(patients, HttpStatus.OK);
    }

    @PostMapping("/specialist/{specialistId}")
    public ResponseEntity<PatientResponseDTO> addPatient(@PathVariable Long specialistId,
                                                 @Valid @RequestBody PatientCreateDTO patientCreateDTO) {

        PatientResponseDTO createdPatient = patientService.create(specialistId, patientCreateDTO);
        allergyService.updateAllergiesInPatient(createdPatient.getId(), patientCreateDTO.getAllergiesGroup());

        return new ResponseEntity<PatientResponseDTO>(createdPatient, HttpStatus.CREATED);
    }

    @PutMapping("{patientId}")
    public ResponseEntity<PatientResponseDTO> updatePatientById(@PathVariable Long patientId,
                                                                @Valid @RequestBody PatientUpdateDTO patientUpdateDTO) {

        PatientResponseDTO updatedPatient = patientService.update(patientId, patientUpdateDTO);
        return new ResponseEntity<PatientResponseDTO>(updatedPatient, HttpStatus.OK);
    }

    @PutMapping("{patientId}/updateNotes")
    public ResponseEntity<PatientResponseDTO> updateNotesById(@PathVariable Long patientId,
                                                              @Valid @RequestBody NotesUpdateDTO notesUpdateDTO) {

        PatientResponseDTO updatedPatient = patientService.updateNotes(patientId, notesUpdateDTO);
        return new ResponseEntity<PatientResponseDTO>(updatedPatient, HttpStatus.OK);
    }

    @PutMapping("/{patientId}/updateMedicalInfo")
    public ResponseEntity<MedicalInformationResponseDTO> updateMedicalInformation(@PathVariable Long patientId,
                                                                       @Valid @RequestBody MedicalInformationUpdateDTO medicalInformationUpdateDTO) {

        MedicalInformationResponseDTO responseDTO = patientService.updateMedicalInformation(patientId, medicalInformationUpdateDTO);
        return new ResponseEntity<MedicalInformationResponseDTO>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("{patientId}")
    public ResponseEntity<Void> deletePatientById(@PathVariable Long patientId) {

        patientService.delete(patientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
