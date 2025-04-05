package com.healthplus.healthplus_api.patient.mapping;

import com.healthplus.healthplus_api.patient.domain.model.entity.MedicalInformation;
import com.healthplus.healthplus_api.patient.dto.MedicalInformationResponseDTO;
import com.healthplus.healthplus_api.patient.dto.MedicalInformationUpdateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicalInformationMapper {

    @Autowired
    ModelMapper modelMapper;

    public MedicalInformation toEntity(MedicalInformationUpdateDTO medicalInformationUpdateDTO) {
        return modelMapper.map(medicalInformationUpdateDTO, MedicalInformation.class);
    }

    public MedicalInformationResponseDTO toResponseDTO(MedicalInformation medicalInformation) {
        return modelMapper.map(medicalInformation, MedicalInformationResponseDTO.class);
    }
}
