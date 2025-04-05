package com.healthplus.healthplus_api.appointment.domain.service;

import com.healthplus.healthplus_api.appointment.dto.AppointmentCreateDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentDateRequestDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentResponseDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentUpdateDTO;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO findById(Long appointmentId);
    AppointmentResponseDTO create(Long specialistId, Long patientId, AppointmentCreateDTO appointmentCreateDTO);
    AppointmentResponseDTO update(Long appointmentId, AppointmentUpdateDTO appointmentUpdateDTO);
    List<AppointmentResponseDTO> findAppointmentsInMonth(Long specialistId, AppointmentDateRequestDTO appointmentDateRequestDTO);
    List<AppointmentResponseDTO> findAppointmentsInWeek(Long specialistId, AppointmentDateRequestDTO appointmentDateRequestDTO);
    void delete(Long appointmentId);
}
