package com.healthplus.healthplus_api.patient.mapping;

import com.healthplus.healthplus_api.patient.domain.model.entity.Patient;
import com.healthplus.healthplus_api.patient.dto.PatientCreateDTO;
import com.healthplus.healthplus_api.patient.dto.PatientDetailsDTO;
import com.healthplus.healthplus_api.patient.dto.PatientResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public PatientMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureMappings();
    }

    private void configureMappings() {
        // Configurar las reglas para ignorar propiedades específicas una sola vez
        modelMapper.addMappings(new PropertyMap<PatientCreateDTO, Patient>() {
            @Override
            protected void configure() {
                skip(destination.getAllergies());
                skip(destination.getCustomAllergies());
            }
        });
    }

    public PatientResponseDTO toResponseDTO(Patient patient) {
        return modelMapper.map(patient, PatientResponseDTO.class);
    }

    public PatientCreateDTO toCreateDTO(Patient patient) {
        return modelMapper.map(patient, PatientCreateDTO.class);
    }

    public Patient toEntity(PatientCreateDTO patientCreateDTO) {
        // Configurar ModelMapper para ignorar allergiesGroup durante el mapeo
        //modelMapper.addMappings(new PropertyMap<PatientCreateDTO, Patient>() {
        //    @Override
        //    protected void configure() {
                // Ignorar allergies y customAllergies para el mapeo
        //        skip(destination.getAllergies());
        //        skip(destination.getCustomAllergies());
        //    }
        //});
        return modelMapper.map(patientCreateDTO, Patient.class);
    }

    public PatientDetailsDTO toDetailsDTO(Patient patient) {
        return modelMapper.map(patient, PatientDetailsDTO.class);
    }
}
