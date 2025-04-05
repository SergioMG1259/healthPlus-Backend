package com.healthplus.healthplus_api.profile.dto;

import com.healthplus.healthplus_api.appointment.dto.AppointmentResponseDTO;
import com.healthplus.healthplus_api.patient.dto.PatientResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class OverviewResponseDTO {

    private Integer totalPatients;

    private Integer totalAppointments;

    private BigDecimal totalEarning;

    private String patientName;

    private List<PatientResponseDTO> patients;

    private List<AppointmentResponseDTO> appointments;

    private ChartDTO patientChart;

    private EarningChartDTO earningChart;

    private ChartDTO genderChart;
}