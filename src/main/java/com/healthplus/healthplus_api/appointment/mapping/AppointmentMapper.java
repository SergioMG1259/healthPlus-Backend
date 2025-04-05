package com.healthplus.healthplus_api.appointment.mapping;

import com.healthplus.healthplus_api.appointment.domain.model.entity.Appointment;
import com.healthplus.healthplus_api.appointment.dto.AppointmentCreateDTO;
import com.healthplus.healthplus_api.appointment.dto.AppointmentResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    @Autowired
    private ModelMapper modelMapper;

    public Appointment toEntity(AppointmentCreateDTO appointmentCreateDTO) {
        return modelMapper.map(appointmentCreateDTO, Appointment.class);
    }

    public AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return modelMapper.map(appointment, AppointmentResponseDTO.class);
    }
}
