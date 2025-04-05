package com.healthplus.healthplus_api.appointment.api;

import com.healthplus.healthplus_api.appointment.domain.model.entity.Appointment;
import com.healthplus.healthplus_api.appointment.domain.service.AppointmentService;
import com.healthplus.healthplus_api.appointment.dto.AppointmentCreateDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentDateRequestDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentResponseDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/appointments")
@PreAuthorize("hasAnyRole('ADMIN', 'SPECIALIST')")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> findAppointmentById(@PathVariable Long appointmentId) {

        AppointmentResponseDTO appointment = appointmentService.findById(appointmentId);
        return new ResponseEntity<AppointmentResponseDTO>(appointment, HttpStatus.OK);
    }

    @PostMapping("/specialist/{specialistId}/patient/{patientId}")
    public ResponseEntity<AppointmentResponseDTO> addAppointment(@PathVariable("specialistId") Long specialistId,
                                                                 @PathVariable("patientId") Long patientId,
                                                                 @Valid @RequestBody AppointmentCreateDTO appointmentCreateDTO) {

        AppointmentResponseDTO createdAppointment = appointmentService.create(specialistId, patientId, appointmentCreateDTO);
        return new ResponseEntity<AppointmentResponseDTO>(createdAppointment, HttpStatus.OK);
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long appointmentId,
                                                                    @Valid @RequestBody AppointmentUpdateDTO appointmentUpdateDTO) {

        AppointmentResponseDTO updatedAppointment = appointmentService.update(appointmentId, appointmentUpdateDTO);
        return new ResponseEntity<AppointmentResponseDTO>(updatedAppointment, HttpStatus.OK);
    }

    @PostMapping("specialist/{specialistId}/monthly")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsMonthlyBySpecialistId(@PathVariable Long specialistId,
                                                            @Valid @RequestBody AppointmentDateRequestDTO appointmentDateRequestDTO) {

        List<AppointmentResponseDTO> appointmentsInMonth = appointmentService.
                                        findAppointmentsInMonth(specialistId, appointmentDateRequestDTO);
        return new ResponseEntity<List<AppointmentResponseDTO>>(appointmentsInMonth, HttpStatus.OK);
    }

    @PostMapping("specialist/{specialistId}/weekly")
    public ResponseEntity<List<AppointmentResponseDTO>> findAppointmentsWeeklyBySpecialistId(@PathVariable Long specialistId,
                                                            @Valid @RequestBody AppointmentDateRequestDTO appointmentDateRequestDTO) {

        List<AppointmentResponseDTO> appointmentsInWeek = appointmentService.
                                        findAppointmentsInWeek(specialistId, appointmentDateRequestDTO);
        return new ResponseEntity<List<AppointmentResponseDTO>>(appointmentsInWeek, HttpStatus.OK);
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Appointment> deleteAppointment(@PathVariable Long appointmentId) {

        appointmentService.delete(appointmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
