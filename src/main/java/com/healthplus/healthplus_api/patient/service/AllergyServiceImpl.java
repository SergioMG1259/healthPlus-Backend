package com.healthplus.healthplus_api.patient.service;

import com.healthplus.healthplus_api.auth.security.PermissionService;
import com.healthplus.healthplus_api.exception.ResourceNotFoundException;
import com.healthplus.healthplus_api.patient.domain.model.entity.Allergy;
import com.healthplus.healthplus_api.patient.domain.model.entity.CustomAllergy;
import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import com.healthplus.healthplus_api.patient.domain.model.entity.PatientAllergy;
import com.healthplus.healthplus_api.patient.domain.persistance.AllergyRepository;
import com.healthplus.healthplus_api.patient.domain.persistance.CustomAllergyRepository;
import com.healthplus.healthplus_api.patient.domain.persistance.PatientAllergyRepository;
import com.healthplus.healthplus_api.patient.domain.persistance.PatientRepository;
import com.healthplus.healthplus_api.patient.domain.service.AllergyService;
import com.healthplus.healthplus_api.patient.dto.AllergyGroupCreateDTO;
import com.healthplus.healthplus_api.patient.dto.AllergyGroupResponseDTO;
import com.healthplus.healthplus_api.patient.dto.AllergyRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AllergyServiceImpl implements AllergyService {
    @Autowired
    private AllergyRepository allergyRepository;
    @Autowired
    private CustomAllergyRepository customAllergyRepository;
    @Autowired
    private PatientAllergyRepository patientAllergyRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PermissionService permissionService;

    @Override
    @Transactional(readOnly = true)
    public List<Allergy> findAll() {
        return allergyRepository.findAll();
    }

    @Override
    @Transactional
    public Allergy create(AllergyRequestDTO allergyRequestDTO) {

        Allergy allergy = new Allergy();
        allergy.setName(allergyRequestDTO.getName());

        return allergyRepository.save(allergy);
    }

    @Override
    @Transactional
    public Allergy update(Long allergyId, AllergyRequestDTO allergyRequestDTO) {

        Allergy allergy = allergyRepository.findById(allergyId)
                .orElseThrow(() -> new ResourceNotFoundException("Allergy not found with ID: " + allergyId));
        allergy.setName(allergyRequestDTO.getName());

        return allergyRepository.save(allergy);
    }

    @Override
    @Transactional
    public void delete(Long allergyId) {

        Allergy allergy = allergyRepository.findById(allergyId)
                .orElseThrow(() -> new ResourceNotFoundException("Allergy not found with ID: " + allergyId));

        allergyRepository.delete(allergy);
    }

    @Override
    @Transactional
    public AllergyGroupResponseDTO updateAllergiesInPatient(Long patientId, AllergyGroupCreateDTO allergyGroupCreateDTO) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (!permissionService.hasSpecialistPermissionToAccess(patient.getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permission to update this patient's allergies");
        }

        // Para actualizar las alergias predeterminadas de un paciente
        List<Allergy> allergies = updatePatientAllergies(patient, allergyGroupCreateDTO.getAllergiesId());

        // Para actualizar las alergias custom de un paciente
        List<CustomAllergy> customAllergies = updatePatientCustomAllergies(patient, allergyGroupCreateDTO.getCustomAllergiesNames());

        patient.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);

        return new AllergyGroupResponseDTO(allergies, customAllergies);
    }

    public List<Allergy> updatePatientAllergies(Patient patient, List<Long> allergiesIds) {

        // Obtener las alergias desde la base de datos, si el ID no coincide, se ignora
        List<Allergy> newAllergies = allergyRepository.findAllById(allergiesIds);
        // Obtener los ID de las alergias solicitadas
        Set<Long> newAllergiesIds = newAllergies.stream()
                .map(Allergy::getId)
                .collect(Collectors.toSet());

        // Obtener las alergias actuales del paciente
        List<PatientAllergy> currentPatientAllergies = patientAllergyRepository.findByPatientId(patient.getId());
        // Obtener los IDs de alergias actuales del paciente
        Set<Long> currentAllergyIds = currentPatientAllergies.stream()
                .map(patientAllergy -> patientAllergy.getAllergy().getId())
                .collect(Collectors.toSet());

        // Filtrar las alergias que deben ser eliminadas (aquellas que no aparecen en la solicitud)
        List<PatientAllergy> allergiesToRemove = currentPatientAllergies.stream()
                .filter(currentPatientAllergy -> !newAllergiesIds.contains(currentPatientAllergy.getAllergy().getId()))
                .toList();

        // Eliminar las alergias que ya no están en la nueva lista (si existiera alguna)
        patientAllergyRepository.deleteAll(allergiesToRemove);

        // Filtrar las alergias que deben ser agregadas (aquellas que no existen actualmente)
        List<PatientAllergy> allergiesToAdd = newAllergies.stream()
                .filter(allergy -> !currentAllergyIds.contains(allergy.getId())) //si no estan agregadas
                .map(allergy -> {
                    PatientAllergy patientAllergy = new PatientAllergy();
                    patientAllergy.setPatient(patient);
                    patientAllergy.setAllergy(allergy);
                    patientAllergy.setCreatedAt(LocalDateTime.now());
                    return patientAllergy;
                })
                .toList();

        // Guardar las nuevas relaciones en la base de datos
        patientAllergyRepository.saveAll(allergiesToAdd);

        List<PatientAllergy> newPatientAllergies = patientAllergyRepository.findByPatientId(patient.getId());
        return newPatientAllergies.stream().map(PatientAllergy::getAllergy)
                .toList();
    }

    public List<CustomAllergy> updatePatientCustomAllergies(Patient patient, List<AllergyRequestDTO> customAllergies) {

        // Al ponerlo en minusculas, no hay problema si escriben la misma alergia en minuscula o mayuscula, solo la considerará una vez
        // Obtener las custom alergias desde la base de datos
        List<CustomAllergy> existingAllergies = customAllergyRepository.findByPatientId(patient.getId());
        // Mapear las alergias actuales a un set de nombres para fácil comparación
        Set<String> currentAllergyNames = existingAllergies.stream()
                .map( customAllergy -> {return customAllergy.getName().toLowerCase();} )
                .collect(Collectors.toSet());

        // Mapear las alergias de la peticion a un set de nombres
        Set<String> requestedAllergyNames = customAllergies.stream()
                .map(allergyRequest -> {return allergyRequest.getName().toLowerCase();}).collect(Collectors.toSet());

        // Identificar custom alergias a eliminar
        List<CustomAllergy> customAllergiesToRemove = existingAllergies.stream()
                .filter(customAllergy -> !requestedAllergyNames.contains(customAllergy.getName()))
                .toList();

        customAllergyRepository.deleteAll(customAllergiesToRemove);

        // Filtrar las custom alergias que deben ser agregadas (aquellas que no existen actualmente)
        List<CustomAllergy> customAllergiesToAdd = requestedAllergyNames.stream()
                .filter(requestAllergyName -> !currentAllergyNames.contains(requestAllergyName))
                .map(name -> {
                    CustomAllergy customAllergy = new CustomAllergy();
                    customAllergy.setName(name.toLowerCase());
                    customAllergy.setPatient(patient);
                    return customAllergy;
                })
                .toList();

        customAllergyRepository.saveAll(customAllergiesToAdd);

        return customAllergyRepository.findByPatientId(patient.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Allergy> findPatientAllergiesByPatientId(Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (!permissionService.hasSpecialistPermissionToAccess(patient.getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permission to access this patient's allergies");
        }

        // TO-DO devolver también las custom alergías
        List<PatientAllergy> patientAllergies = patientAllergyRepository.findByPatientId(patientId);

        return patientAllergies.stream()
                .map(PatientAllergy::getAllergy)
                .collect(Collectors.toList());
    }
}
