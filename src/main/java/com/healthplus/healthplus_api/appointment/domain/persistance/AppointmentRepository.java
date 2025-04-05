package com.healthplus.healthplus_api.appointment.domain.persistance;

import com.healthplus.healthplus_api.appointment.domain.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.patient.specialist.id = :specialistId " +
            "AND a.startDate < :endDate " +
            "AND a.endDate > :startDate")
    Boolean existsAppointmentInRange(@Param("specialistId") Long specialistId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.patient.specialist.id = :specialistId " +
            "AND a.startDate < :endDate " +
            "AND a.endDate > :startDate " +
            "AND a.id != :id")
    Boolean existsAppointmentInRangeForUpdate(@Param("specialistId") Long specialistId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("id") Long id);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.specialist.id = :specialistId " +
            "AND a.startDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.startDate DESC")
    List<Appointment> findBySpecialistIdAndDateRange(@Param("specialistId") Long specialistId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.specialist.id = :specialistId " +
            "ORDER BY a.createdAt DESC")
    List<Appointment> findAllBySpecialistId(@Param("specialistId") Long specialistId);

    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Appointment a " +
            "WHERE a.patient.specialist.id = :specialistId " +
            "AND a.endDate <= :currentDateTime")
    BigDecimal findTotalEarningBySpecialistId(@Param("specialistId") Long specialistId,
                                           @Param("currentDateTime") LocalDateTime currentDateTime);

}
