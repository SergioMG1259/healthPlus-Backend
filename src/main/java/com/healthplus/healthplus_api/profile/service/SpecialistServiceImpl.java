package com.healthplus.healthplus_api.profile.service;

import com.healthplus.healthplus_api.appointment.domain.model.entity.Appointment;
import com.healthplus.healthplus_api.appointment.domain.persistance.AppointmentRepository;
import com.healthplus.healthplus_api.appointment.mapping.AppointmentMapper;
import com.healthplus.healthplus_api.auth.domain.model.entity.User;
import com.healthplus.healthplus_api.auth.domain.persistance.UserRepository;
import com.healthplus.healthplus_api.auth.security.PermissionService;
import com.healthplus.healthplus_api.exception.BadRequestException;
import com.healthplus.healthplus_api.exception.ResourceNotFoundException;
import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import com.healthplus.healthplus_api.patient.domain.model.enums.Gender;
import com.healthplus.healthplus_api.patient.domain.persistance.PatientRepository;
import com.healthplus.healthplus_api.patient.mapping.PatientMapper;
import com.healthplus.healthplus_api.profile.domain.model.entity.Specialist;
import com.healthplus.healthplus_api.profile.domain.persistance.SpecialistRepository;
import com.healthplus.healthplus_api.profile.domain.service.SpecialistService;
import com.healthplus.healthplus_api.profile.dto.*;
import com.healthplus.healthplus_api.profile.mapping.SpecialistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpecialistServiceImpl implements SpecialistService {

    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private SpecialistMapper specialistMapper;

    @Override
    @Transactional(readOnly = true)
    public SpecialistResponseDTO findById(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permission to access this specialist profile");
        }

        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toResponseDTO(specialist);
        specialistResponseDTO.setEmail(specialist.getUser().getEmail());

        return specialistResponseDTO;
    }

    @Override
    @Transactional
    public SpecialistResponseDTO update(Long specialistId, SpecialistUpdateDTO specialistUpdateDTO) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permission to update this specialist profile");
        }

        if(userRepository.existsByEmailAndIdNot(specialistUpdateDTO.getEmail(), specialist.getUser().getId())) {
            throw new BadRequestException("The email is already registered");
        }

        if (specialistRepository.existsByNamesAndLastNamesAndIdNot(specialistUpdateDTO.getNames(), specialistUpdateDTO.getLastNames(),
                specialist.getId())) {
            throw new BadRequestException("The names and last names is already registered");
        }

        User user = specialist.getUser();
        specialist.setNames(specialistUpdateDTO.getNames());
        specialist.setLastNames(specialistUpdateDTO.getLastNames());
        specialist.setBirthDate(specialistUpdateDTO.getBirthDate());
        specialist.setSpecialty(specialistUpdateDTO.getSpecialty());
        specialist.setMedicalInstitution(specialistUpdateDTO.getMedicalInstitution());
        specialist.setYearsOfExperience(specialistUpdateDTO.getYearsOfExperience());
        user.setEmail(specialistUpdateDTO.getEmail());
        specialist.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toResponseDTO(specialist);
        specialistResponseDTO.setEmail(user.getEmail());

        return specialistResponseDTO;
    }

    @Override
    public OverviewResponseDTO overview(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permission to update this specialist profile");
        }

        List<Patient> patients = patientRepository.findBySpecialistId(specialistId);

        List<Appointment> appointments = appointmentRepository.findAllBySpecialistId(specialistId);

        Integer totalPatients = patients.size();

        Integer totalAppointments = appointments.size();

        BigDecimal totalEarning = appointmentRepository.findTotalEarningBySpecialistId(specialistId, LocalDateTime.now());

        String patientNames = specialist.getNames();

        List<Patient> lastPatients = patients.stream().limit(5).toList();

        List<Appointment> appointmentsToday = appointmentRepository.
                findBySpecialistIdAndDateRange(specialistId, LocalDateTime.now().with(LocalTime.of(6, 0)),
                        LocalDateTime.now().with(LocalTime.of(23, 0)));

        ChartDTO patientChart = getPatientsChart(patients);

        ChartDTO genderChart = getGenderChart(patients);

        EarningChartDTO earningChart = getEarningChart(appointments);

        return new OverviewResponseDTO(totalPatients, totalAppointments, totalEarning, patientNames,
                lastPatients.stream().map(patientMapper::toResponseDTO).toList(),
                appointmentsToday.stream().map(appointmentMapper::toResponseDTO).toList(), patientChart, earningChart,genderChart);
    }

    public ChartDTO getPatientsChart(List<Patient> patients) {

        // Obtener la fecha actual
        LocalDate now = LocalDate.now();

        // Crear un mapa con los últimos 12 meses
        Map<YearMonth, Integer> patientsPerMonth = new LinkedHashMap<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            patientsPerMonth.put(yearMonth, 0); // Inicializa los meses en 0 pacientes
        }

        // Contar los pacientes por mes
        for (Patient patient : patients) {
            // Extrae el año y mes en el que fue creado el paciente
            YearMonth patientMonth = YearMonth.from(patient.getCreatedAt().toLocalDate());
            if (patientsPerMonth.containsKey(patientMonth)) { // Si el mes y el año está en el mapa
                patientsPerMonth.put(patientMonth, patientsPerMonth.get(patientMonth) + 1); // Se obtiene el valor actual y se aumenta 1
            }
        }

        // Extraer nombres de los meses en inglés (3 caracteres)
        List<String> monthNames = patientsPerMonth.keySet().stream()
                .map(ym -> ym.getMonth().name().substring(0, 3)) // "JAN", "FEB", etc.
                .map(String::toLowerCase) // Convertir a minúsculas
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)) // Capitalizar primera letra
                .toList();

        List<Integer> patientCounts = new ArrayList<>(patientsPerMonth.values());

        return new ChartDTO(monthNames, patientCounts);
    }

    public ChartDTO getGenderChart(List<Patient> patients) {

        Map<Gender, Long> genderCount = patients.stream()
                .collect(Collectors.groupingBy(Patient::getGender, Collectors.counting()));

        List<String> genders = List.of("Male", "Female");
        List<Integer> counts = List.of(
                genderCount.getOrDefault(Gender.MALE, 0L).intValue(),
                genderCount.getOrDefault(Gender.FEMALE, 0L).intValue()
        );

        return new ChartDTO(genders, counts);
    }

    public EarningChartDTO getEarningChart(List<Appointment> appointments) {

        // Obtener la fecha actual
        LocalDateTime now = LocalDateTime.now();

        // Crear un mapa con los últimos 12 meses
        Map<YearMonth, BigDecimal> earningsPerMonth  = new LinkedHashMap<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            earningsPerMonth .put(yearMonth, BigDecimal.ZERO); // Inicializa los meses en 0 pacientes
        }

        for(Appointment appointment : appointments) {
            if (appointment.getEndDate().isBefore(now)) { // Solo si ya terminó
                YearMonth month = YearMonth.from(appointment.getEndDate().toLocalDate());
                if(earningsPerMonth.containsKey(month)) {
                    earningsPerMonth.put(month, earningsPerMonth.get(month).add(appointment.getPrice()));
                }
            }
        }

        // Extraer nombres de los meses en inglés (3 caracteres)
        List<String> monthNames = earningsPerMonth.keySet().stream()
                .map(ym -> ym.getMonth().name().substring(0, 3)) // "JAN", "FEB", etc.
                .map(String::toLowerCase) // Convertir a minúsculas
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)) // Capitalizar primera letra
                .toList();

        List<BigDecimal> earningsCount = new ArrayList<>(earningsPerMonth.values());

        return new EarningChartDTO(monthNames, earningsCount);
    }
}
