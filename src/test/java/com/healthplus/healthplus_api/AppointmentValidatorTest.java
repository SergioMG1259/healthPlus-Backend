package com.healthplus.healthplus_api;

import com.healthplus.healthplus_api.appointment.service.AppointmentValidator;
import com.healthplus.healthplus_api.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;

@SpringBootTest
public class AppointmentValidatorTest {

    private final AppointmentValidator validator = new AppointmentValidator();

    @Test
    public void whenDaysAreDifferent() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 4, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 5, 10, 0);

        assertThrows(BadRequestException.class, () -> {
            validator.validateSameDay(start, end);
        });
    }

    @Test
    public void whenStartDateIsAfterEndDate() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 5, 11, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 5, 10, 0);

        assertThrows(BadRequestException.class, () -> {
            validator.validateEndAfterStart(start, end);
        });
    }

    @Test
    public void whenDatesAreEquals() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 5, 11, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 5, 11, 0);

        assertThrows(BadRequestException.class, () -> {
            validator.validateDifferentTimes(start, end);
        });
    }

    @Test
    public void whenAppointmentLastsLessThanOneHour() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 5, 11, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 5, 11, 30);

        assertThrows(BadRequestException.class, () -> {
            validator.validateMinDuration(start, end);
        });
    }

    @Test
    public void whenAppointmentOutsideWorkingHours() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 5, 5, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 5, 7, 30);

        assertThrows(BadRequestException.class, () -> {
            validator.validateWorkingHours(start, end);
        });
    }

    @Test
    public void whenDatesAreNotExact() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 5, 11, 25);
        LocalDateTime end = LocalDateTime.of(2024, 4, 5, 12, 0);

        assertThrows(BadRequestException.class, () -> {
            validator.validateExactHours(start, end);
        });
    }

    @Test
    public void whenAppointmentIsInPast() {
        LocalDateTime start = LocalDateTime.of(2025, 4, 4, 11, 0);

        assertThrows(BadRequestException.class, () -> {
            validator.validateFutureDate(start);
        });
    }
}
