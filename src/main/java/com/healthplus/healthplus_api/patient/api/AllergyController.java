package com.healthplus.healthplus_api.patient.api;

import com.healthplus.healthplus_api.patient.domain.model.entity.Allergy;
import com.healthplus.healthplus_api.patient.domain.service.AllergyService;
import com.healthplus.healthplus_api.patient.dto.AllergyGroupCreateDTO;
import com.healthplus.healthplus_api.patient.dto.AllergyGroupResponseDTO;
import com.healthplus.healthplus_api.patient.dto.AllergyRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/allergies")
@PreAuthorize("hasAnyRole('ADMIN', 'SPECIALIST')")
public class AllergyController {

    @Autowired
    private AllergyService allergyService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Allergy>> findAllAllergies() {

        List<Allergy> allergies = this.allergyService.findAll();
        return new ResponseEntity<List<Allergy>>(allergies, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Allergy> addAllergy(@RequestBody AllergyRequestDTO allergyRequestDTO) {

        Allergy allergy = allergyService.create(allergyRequestDTO);
        return new ResponseEntity<Allergy>(allergy, HttpStatus.CREATED);
    }

    @PutMapping("/{allergyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Allergy> updateAllergy(@PathVariable Long allergyId, @RequestBody AllergyRequestDTO allergyRequestDTO) {

        Allergy allergy = allergyService.update(allergyId, allergyRequestDTO);
        return new ResponseEntity<Allergy>(allergy, HttpStatus.OK);
    }

    @DeleteMapping("/{allergyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Allergy> deleteAllergy(@PathVariable Long allergyId) {

        allergyService.delete(allergyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Allergy>> findAllergiesByPatient(@PathVariable Long patientId) {
        // TODO: hacer que devuelva tambien los custom allergies o eliminar este endpoint
        List<Allergy> allergies = allergyService.findPatientAllergiesByPatientId(patientId);
        return new ResponseEntity<List<Allergy>>(allergies, HttpStatus.OK);
    }

    @PutMapping("/patient/{patientId}")
    public ResponseEntity<AllergyGroupResponseDTO> updateAllergiesInPatient(@PathVariable Long patientId,
                                                                     @Valid @RequestBody AllergyGroupCreateDTO allergyGroupCreateDTO) {

        AllergyGroupResponseDTO allergies = allergyService.updateAllergiesInPatient(patientId, allergyGroupCreateDTO);
        return new ResponseEntity<AllergyGroupResponseDTO>(allergies, HttpStatus.OK);
    }

    //@PostMapping("/patient/{patientId}")
    //public ResponseEntity<List<Allergy>> addPatientAllergy(@PathVariable Long patientId,
    //                                                       @Valid @RequestBody PatientAllergyCreateDTO patientAllergyCreateDTO) {

    //    List<Allergy> allergies = allergyService.updatePatientAllergies(patientId,patientAllergyCreateDTO);
    //    return new ResponseEntity<List<Allergy>>(allergies, HttpStatus.OK);
    //}

    //@PostMapping("/custom/patient/{patientId}")
    //public ResponseEntity<List<CustomAllergy>> addCustomAllergies(@PathVariable Long patientId,
    //                                                              @Valid @RequestBody CustomAllergiesCreateDTO customAllergiesCreateDTO) {

    //    List<CustomAllergy> customAllergies = allergyService.updatePatientCustomAllergies(patientId,customAllergiesCreateDTO);
    //    return new ResponseEntity<List<CustomAllergy>>(customAllergies, HttpStatus.OK);
    //}
}
