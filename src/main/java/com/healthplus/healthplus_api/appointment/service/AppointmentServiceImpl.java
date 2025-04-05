package com.healthplus.healthplus_api.appointment.service;

import com.healthplus.healthplus_api.appointment.domain.model.entity.Appointment;
import com.healthplus.healthplus_api.appointment.domain.persistance.AppointmentRepository;
import com.healthplus.healthplus_api.appointment.domain.service.AppointmentService;
import com.healthplus.healthplus_api.appointment.dto.AppointmentCreateDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentDateRequestDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentResponseDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentUpdateDTO;
import com.healthplus.healthplus_api.appointment.mapping.AppointmentMapper;
import com.healthplus.healthplus_api.auth.security.PermissionService;
import com.healthplus.healthplus_api.exception.BadRequestException;
import com.healthplus.healthplus_api.exception.ResourceNotFoundException;
import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import com.healthplus.healthplus_api.patient.domain.persistance.PatientRepository;
import com.healthplus.healthplus_api.profile.domain.model.entity.Specialist;
import com.healthplus.healthplus_api.profile.domain.persistance.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    private AppointmentValidator appointmentValidator;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private PermissionService permissionService;

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO findById(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!permissionService.hasSpecialistPermissionToAccess(appointment.getPatient().getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permissions to access the appointment details");
        }

        return appointmentMapper.toResponseDTO(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO create(Long specialistId, Long patientId, AppointmentCreateDTO appointmentCreateDTO) {

        Appointment appointment = appointmentMapper.toEntity(appointmentCreateDTO);

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permissions to create this appointment");
        }

        // Verificar que el paciente este asignado al especialista
        if (!Objects.equals(patient.getSpecialist().getId(), specialist.getId())) {
            throw new AccessDeniedException("The patient is not assigned to the specialist. Only assigned patients can have appointments created");
        }

        // Realizar validaciones de fechas
        validateDates(appointment.getStartDate(), appointment.getEndDate());

        // Verificar si hay una cita existente en el mismo rango de fechas para el especialista
        boolean isOverlap = appointmentRepository.existsAppointmentInRange(
                specialist.getId(), appointment.getStartDate(), appointment.getEndDate());
        if (isOverlap) {
            throw new BadRequestException("An appointment already exists in this time slot");
        }

        appointment.setPatient(patient);
        appointment.setCreatedAt(LocalDateTime.now());
        Appointment createdAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponseDTO(createdAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO update(Long appointmentId, AppointmentUpdateDTO appointmentUpdateDTO) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!permissionService.hasSpecialistPermissionToAccess(appointment.getPatient().getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permissions to update this appointment");
        }

        // Realizar validaciones de fechas
        validateDates(appointmentUpdateDTO.getStartDate(), appointmentUpdateDTO.getEndDate());

        // No se pueden editar citas anteriores
        if (appointment.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Past appointments cannot be edited");
        }

        // Verificar si hay una cita existente en el mismo rango de fechas para el especialista
        boolean isOverlap = appointmentRepository.existsAppointmentInRangeForUpdate(
                appointment.getPatient().getSpecialist().getId(), appointmentUpdateDTO.getStartDate(),
                appointmentUpdateDTO.getEndDate(), appointment.getId());
        if (isOverlap) {
            throw new BadRequestException("An appointment already exists in this time slot");
        }

        appointment.setPrice(appointmentUpdateDTO.getPrice());
        appointment.setIssue(appointmentUpdateDTO.getIssue());
        appointment.setStartDate(appointmentUpdateDTO.getStartDate());
        appointment.setEndDate(appointmentUpdateDTO.getEndDate());
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(updatedAppointment);
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        appointmentValidator.validateSameDay(startDate, endDate);
        appointmentValidator.validateEndAfterStart(startDate, endDate);
        appointmentValidator.validateDifferentTimes(startDate, endDate);
        appointmentValidator.validateMinDuration(startDate, endDate);
        appointmentValidator.validateWorkingHours(startDate, endDate);
        appointmentValidator.validateExactHours(startDate, endDate);
        appointmentValidator.validateFutureDate(startDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findAppointmentsInMonth(Long specialistId, AppointmentDateRequestDTO appointmentDateRequestDTO) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permissions to view the month's appointments");
        }

        // Obtener el primer y último día del mes
        LocalDate firstDayOfMonth = appointmentDateRequestDTO.getDate().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = appointmentDateRequestDTO.getDate().toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime firstDayOfMonthDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime lastDayOfMonthDateTime = lastDayOfMonth.atTime(23, 59, 59);

        List<Appointment> appointmentsInMonth = appointmentRepository
                .findBySpecialistIdAndDateRange(specialist.getId(), firstDayOfMonthDateTime, lastDayOfMonthDateTime);

        return appointmentsInMonth.stream().map(appointmentMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findAppointmentsInWeek(Long specialistId, AppointmentDateRequestDTO appointmentDateRequestDTO) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permissions to view the week's appointments");
        }

        LocalDate firstDayOfWeek = appointmentDateRequestDTO.getDate().toLocalDate().with(DayOfWeek.SUNDAY);
        LocalDate lastDayOfWeek = appointmentDateRequestDTO.getDate().toLocalDate().with(DayOfWeek.SATURDAY);

        // Si el domingo calculado es después de la fecha, se ajusta a la semana anterior
        if (firstDayOfWeek.isAfter(appointmentDateRequestDTO.getDate().toLocalDate())) {
            firstDayOfWeek = firstDayOfWeek.minusWeeks(1);
        }

        // Si el sábado calculado es antes de la fecha, se ajusta a la semana siguiente
        if (lastDayOfWeek.isBefore(appointmentDateRequestDTO.getDate().toLocalDate())) {
            lastDayOfWeek = lastDayOfWeek.plusWeeks(1);
        }

        LocalDateTime firstDayOfWeekDateTime = firstDayOfWeek.atStartOfDay();
        LocalDateTime lastDayOfWeekDateTime = lastDayOfWeek.atTime(23, 59, 59);

        List<Appointment> appointmentsInWeek = appointmentRepository
                .findBySpecialistIdAndDateRange(specialist.getId(), firstDayOfWeekDateTime, lastDayOfWeekDateTime);

        return appointmentsInWeek.stream().map(appointmentMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public void delete(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!permissionService.hasSpecialistPermissionToAccess(appointment.getPatient().getSpecialist().getId())) {
            throw new AccessDeniedException("You do not have permissions to delete this appointment");
        }

        appointmentRepository.delete(appointment);
    }

}
