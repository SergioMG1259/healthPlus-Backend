package com.healthplus.healthplus_api.profile.mapping;

import com.healthplus.healthplus_api.profile.domain.model.entity.Specialist;
import com.healthplus.healthplus_api.profile.dto.SpecialistResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialistMapper {

    @Autowired
    private ModelMapper modelMapper;

    public SpecialistResponseDTO toResponseDTO(Specialist specialist) {
        return modelMapper.map(specialist, SpecialistResponseDTO.class);
    }
}
