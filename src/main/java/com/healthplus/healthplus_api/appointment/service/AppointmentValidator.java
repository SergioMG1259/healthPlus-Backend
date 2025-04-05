package com.healthplus.healthplus_api.appointment.service;

import com.healthplus.healthplus_api.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Component
public class AppointmentValidator {
    private static final LocalTime WORKING_HOURS_START = LocalTime.of(6, 0);  // 6 AM
    private static final LocalTime WORKING_HOURS_END = LocalTime.of(23, 0);   // 23 PM

    // Valida si las fechas de inicio y fin son del mismo día
    public void validateSameDay(LocalDateTime startDate, LocalDateTime endDate) {
        if (!startDate.toLocalDate().isEqual(endDate.toLocalDate())) {
            throw new BadRequestException("Start and end date must be the same day");
        }
    }

    // Valida si la hora de fin es posterior a la hora de inicio
    public void validateEndAfterStart(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date/time must be after start date/time");
        }
    }

    // Valida si las horas de inicio y fin son distintas
    public void validateDifferentTimes(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.toLocalTime().equals(endDate.toLocalTime())) {
            throw new BadRequestException("Start and end times must be different");
        }
    }

    // Valida que la duración mínima de la cita sea 1 hora
    public void validateMinDuration(LocalDateTime startDate, LocalDateTime endDate) {
        long durationInMinutes = ChronoUnit.MINUTES.between(startDate, endDate);
        if (durationInMinutes < 60) {
            throw new BadRequestException("The appointment must last at least 1 hour");
        }
    }

    // Valida que la hora de inicio esté dentro del horario laboral (6 AM a 23 PM)
    public void validateWorkingHours(LocalDateTime startDate, LocalDateTime endDate) {
        LocalTime startTime = startDate.toLocalTime();
        LocalTime endTime = endDate.toLocalTime();

        if (startTime.isBefore(WORKING_HOURS_START) || startTime.isAfter(WORKING_HOURS_END) ||
                endTime.isBefore(WORKING_HOURS_START) || endTime.isAfter(WORKING_HOURS_END)) {
            throw new BadRequestException("Appointment start and end time must be between 6:00 AM and 23:00 PM");
        }
    }

    // Valida que las horas sean exactas
    public void validateExactHours(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.getMinute() != 0 || endDate.getMinute() != 0) {
            throw new BadRequestException("Start and end times must be exact hours (e.g., 6:00, 20:00)");
        }
    }

    // Valida que la fecha/hora de inicio esté en el futuro
    public void validateFutureDate(LocalDateTime startDate) {
        LocalDateTime now = LocalDateTime.now();
        if (startDate.isBefore(now)) {
            throw new BadRequestException("The appointment must be scheduled in the future");
        }
    }
}
