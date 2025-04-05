package com.healthplus.healthplus_api.profile.domain.service;

import com.healthplus.healthplus_api.profile.dto.OverviewResponseDTO;
import com.healthplus.healthplus_api.profile.dto.SpecialistResponseDTO;
import com.healthplus.healthplus_api.profile.dto.SpecialistUpdateDTO;

public interface SpecialistService {
    SpecialistResponseDTO findById(Long specialistId);
    SpecialistResponseDTO update(Long specialistId, SpecialistUpdateDTO specialistUpdateDTO);
    OverviewResponseDTO overview(Long specialistId);
}
