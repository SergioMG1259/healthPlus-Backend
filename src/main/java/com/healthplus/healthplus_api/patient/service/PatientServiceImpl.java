package com.healthplus.healthplus_api.patient.service;

import com.healthplus.healthplus_api.auth.security.PermissionService;
import com.healthplus.healthplus_api.exception.BadRequestException;
import com.healthplus.healthplus_api.exception.ResourceNotFoundException;
import com.healthplus.healthplus_api.patient.domain.model.entity.MedicalInformation;
import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import com.healthplus.healthplus_api.patient.domain.model.enums.Gender;
import com.healthplus.healthplus_api.patient.domain.persistance.MedicalInformationRepository;
import com.healthplus.healthplus_api.patient.domain.persistance.PatientRepository;
import com.healthplus.healthplus_api.patient.domain.service.PatientService;
import com.healthplus.healthplus_api.patient.dto.*;
import com.healthplus.healthplus_api.patient.mapping.MedicalInformationMapper;
import com.healthplus.healthplus_api.patient.mapping.PatientMapper;
import com.healthplus.healthplus_api.profile.domain.model.entity.Specialist;
import com.healthplus.healthplus_api.profile.domain.persistance.SpecialistRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    private MedicalInformationRepository medicalInformationRepository;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private MedicalInformationMapper medicalInformationMapper;
    @Autowired
    private PermissionService permissionService;

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findAll() {

        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(patientMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> findAll(Pageable pageable) {

        Page<Patient> patients = patientRepository.findAll(pageable);
        return patients.map(patientMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findBySpecialistId(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + specialistId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permission to access this specialist's patient list");
        }

        List<Patient> patients = patientRepository.findBySpecialistId(specialistId);

        return patients.stream().map(patientMapper::toResponseDTO).toList();
    }

    public Specification<Patient> filter(Long specialistId, String searchByNameAndLastName, boolean female,
                                         boolean male, Integer minAge, Integer maxAge) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nombre y apellidos
            if (searchByNameAndLastName != null && !searchByNameAndLastName.isEmpty()) {
                String[] searchTerms = searchByNameAndLastName.toLowerCase().split("\\s+");
                List<Predicate> subPredicates = new ArrayList<>();

                for (String term : searchTerms) {
                    String searchPattern = "%" + term + "%";
                    subPredicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(criteriaBuilder.concat(root.get("names"),
                                    criteriaBuilder.concat(" ", root.get("lastNames")))),
                            searchPattern
                    ));
                }

                predicates.add(criteriaBuilder.and(subPredicates.toArray(new Predicate[0])));
            }

            // Filtro por género
            if (female && !male) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), Gender.FEMALE));
            } else if (male && !female) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), Gender.MALE));
            }

            // Filtro por rango de edad
            LocalDateTime today = LocalDateTime.now();
            if (minAge != null) {
                LocalDateTime minBirthDate = today.minusYears(minAge);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), minBirthDate));
            }
            if (maxAge != null) {
                LocalDateTime maxBirthDate = today.minusYears(maxAge + 1).plusDays(1);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), maxBirthDate));
            }

            // Filtro por specialistId
            if (specialistId != null) {
                predicates.add(criteriaBuilder.equal(root.get("specialist").get("id"), specialistId));
            }

            // Devuelve todos los predicados combinados
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findBySpecialistIdFilter(Long specialistId, String searchByNameAndLastName, boolean female,
                                                             boolean male, Integer minAge, Integer maxAge, String sortBy) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permission to access this specialist's patient list");
        }

        Specification<Patient> spec = filter(specialistId, searchByNameAndLastName, female, male, minAge, maxAge);
        // Ordenamiento
        Sort sort = Sort.unsorted();
        sort = switch (sortBy) {
            case "age asc" ->
                    Sort.by(Sort.Direction.DESC, "birthDate"); // Más recientes (nacidos después) son más jóvenes
            case "age dsc" -> Sort.by(Sort.Direction.ASC, "birthDate"); // Más antiguos (nacidos antes) son más viejos
            case "created asc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "created dsc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> sort;
        };

        List<Patient> patients = patientRepository.findAll(spec, sort);
        return patients.stream().map(patientMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDetailsDTO findById(Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (!permissionService.hasSpecialistPermissionToAccess(patient.getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permissions to access the details of this patient");
        }

        return patientMapper.toDetailsDTO(patient);
    }

    @Override
    @Transactional
    public PatientResponseDTO create(Long specialistId, PatientCreateDTO patientCreateDTO) {

        Patient patient = patientMapper.toEntity(patientCreateDTO);

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permissions to create this patient");
        }

        if (patientRepository.existsByDniAndSpecialistId(patient.getDni(), specialist.getId())) {
            throw new BadRequestException("The DNI is already registered");
        }

        if (patientRepository.existsByEmailAndSpecialistId(patient.getEmail(), specialist.getId())) {
            throw new BadRequestException("The email is already registered");
        }

        if (patientRepository.existsByNamesAndLastNamesAndSpecialistId(patient.getNames(), patient.getLastNames(), specialist.getId())) {
            throw new BadRequestException("The names and last names is already registered");
        }

        if (patient.getBirthDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("The patient's date of birth must be before today");
        }

        MedicalInformation medicalInformation = getMedicalInformation(patient);

        patient.setSpecialist(specialist);
        patient.setMedicalInformation(medicalInformation);
        patient.setCreatedAt(LocalDateTime.now());

        patientRepository.save(patient);

        return patientMapper.toResponseDTO(patient);
    }

    private MedicalInformation getMedicalInformation(Patient patient) {

        MedicalInformation medicalInformation = new MedicalInformation();

        medicalInformation.setHeight(BigDecimal.valueOf(0.0));
        medicalInformation.setWeight(BigDecimal.valueOf(0.0));
        medicalInformation.setBloodSugar(BigDecimal.valueOf(0.0));
        medicalInformation.setSystolicPressure(0);
        medicalInformation.setDiastolicPressure(0);
        medicalInformation.setHeartRate(0);
        medicalInformation.setCholesterol(BigDecimal.valueOf(0.0));
        medicalInformation.setBMI(BigDecimal.valueOf(0.0));
        medicalInformation.setPatient(patient);
        medicalInformation.setCreatedAt(LocalDateTime.now());

        return medicalInformation;
    }

    @Override
    @Transactional
    public PatientResponseDTO update(Long patientId, PatientUpdateDTO patientUpdateDTO) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (!permissionService.hasSpecialistPermissionToAccess(patient.getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permissions to update this patient");
        }

        if (patientRepository.existsByDniAndIdNotAndSpecialistId(patientUpdateDTO.getDni(), patient.getId(),
                patient.getSpecialist().getId())) {
            throw new BadRequestException("The DNI is already registered");
        }

        if (patientRepository.existsByEmailAndIdNotAndSpecialistId(patientUpdateDTO.getEmail(), patient.getId(),
                patient.getSpecialist().getId())) {
            throw new BadRequestException("The email is already registered");
        }

        if (patientRepository.existsByNamesAndLastNamesAndIdNotAndSpecialistId(patientUpdateDTO.getNames(), patientUpdateDTO.getLastNames(),
                patient.getId(), patient.getSpecialist().getId())) {
            throw new BadRequestException("The names and last names is already registered");
        }

        if (patientUpdateDTO.getBirthDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("The patient's date of birth must be before today");
        }

        patient.setNames(patientUpdateDTO.getNames());
        patient.setLastNames(patientUpdateDTO.getLastNames());
        patient.setAddress(patientUpdateDTO.getAddress());
        patient.setGender(patientUpdateDTO.getGender());
        patient.setBirthDate(patientUpdateDTO.getBirthDate());
        patient.setDni(patientUpdateDTO.getDni());
        patient.setPhoneNumber(patientUpdateDTO.getPhoneNumber());
        patient.setEmail(patientUpdateDTO.getEmail());
        patient.setUpdatedAt(LocalDateTime.now());

        Patient updatedPatient = patientRepository.save(patient);

        return patientMapper.toResponseDTO(updatedPatient);
    }

    @Override
    @Transactional
    public PatientResponseDTO updateNotes(Long patientId, NotesUpdateDTO notesUpdateDTO) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (!permissionService.hasSpecialistPermissionToAccess(patient.getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permissions to update this patient's notes");
        }

        patient.setNotes(notesUpdateDTO.getNotes());
        patient.setUpdatedAt(LocalDateTime.now());

        Patient updatedPatient = patientRepository.save(patient);

        return patientMapper.toResponseDTO(updatedPatient);
    }

    @Override
    @Transactional
    public MedicalInformationResponseDTO updateMedicalInformation(Long patientId, MedicalInformationUpdateDTO medicalInformationUpdateDTO) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (!permissionService.hasSpecialistPermissionToAccess(patient.getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permissions to update this medical information");
        }

        MedicalInformation medicalInformation = patient.getMedicalInformation();

        medicalInformation.setHeight(medicalInformationUpdateDTO.getHeight());
        medicalInformation.setWeight(medicalInformationUpdateDTO.getWeight());
        medicalInformation.setCholesterol(medicalInformationUpdateDTO.getCholesterol());
        medicalInformation.setBloodSugar(medicalInformationUpdateDTO.getBloodSugar());
        medicalInformation.setSystolicPressure(medicalInformationUpdateDTO.getSystolicPressure());
        medicalInformation.setDiastolicPressure(medicalInformationUpdateDTO.getDiastolicPressure());
        medicalInformation.setHeartRate(medicalInformationUpdateDTO.getHeartRate());
        medicalInformation.setBMI(getBMI(medicalInformationUpdateDTO.getWeight(), medicalInformationUpdateDTO.getHeight()));
        medicalInformation.setUpdatedAt(LocalDateTime.now());

        medicalInformationRepository.save(medicalInformation);

        patient.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);

        return medicalInformationMapper.toResponseDTO(medicalInformation);
    }

    private BigDecimal getBMI(BigDecimal weight, BigDecimal height) {

        if (height.compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("The height cannot be zero");
        }

        BigDecimal heightSquared = height.multiply(height);
        return weight.divide(heightSquared, 1, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void delete(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (!permissionService.hasSpecialistPermissionToAccess(patient.getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permissions to delete this patient");
        }

        patientRepository.delete(patient);
    }
}
