package com.healthplus.healthplus_api.auth.domain.service;

import com.healthplus.healthplus_api.auth.dto.*;

public interface UserService {
    LoginResponseDTO registerSpecialist(UserCreateDTO userCreateDTO);
    LoginResponseDTO registerAdmin(UserCreateDTO userCreateDTO);
    LoginResponseDTO login(LoginDTO loginDTO);
    AccessTokenResponseDTO refresh(String refreshToken);
    Boolean changePassword(Long specialistId, ChangePasswordDTO changePasswordDTO);
}
