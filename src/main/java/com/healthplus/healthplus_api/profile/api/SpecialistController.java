package com.healthplus.healthplus_api.profile.api;

import com.healthplus.healthplus_api.profile.domain.service.SpecialistService;
import com.healthplus.healthplus_api.profile.dto.OverviewResponseDTO;
import com.healthplus.healthplus_api.profile.dto.SpecialistResponseDTO;
import com.healthplus.healthplus_api.profile.dto.SpecialistUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/specialists")
@PreAuthorize("hasAnyRole('ADMIN', 'SPECIALIST')")
public class SpecialistController {

    @Autowired
    private SpecialistService specialistService;

    @GetMapping("/{specialistId}")
    public ResponseEntity<SpecialistResponseDTO> findSpecialistById(@PathVariable Long specialistId) {

        SpecialistResponseDTO responseDTO = specialistService.findById(specialistId);
        return new ResponseEntity<SpecialistResponseDTO>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/{specialistId}")
    public ResponseEntity<SpecialistResponseDTO> updateSpecialist(@PathVariable Long specialistId,
                                                                  @Valid @RequestBody SpecialistUpdateDTO specialistUpdateDTO) {

        SpecialistResponseDTO updatedSpecialist = specialistService.update(specialistId, specialistUpdateDTO);
        return new ResponseEntity<SpecialistResponseDTO>(updatedSpecialist, HttpStatus.OK);
    }

    @GetMapping("/overview/{specialistId}")
    public ResponseEntity<OverviewResponseDTO> overview(@PathVariable Long specialistId) {

        OverviewResponseDTO overviewResponseDTO = specialistService.overview(specialistId);
        return new ResponseEntity<OverviewResponseDTO>(overviewResponseDTO, HttpStatus.OK);
    }
}
