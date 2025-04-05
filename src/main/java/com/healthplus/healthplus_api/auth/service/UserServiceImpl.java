package com.healthplus.healthplus_api.auth.service;

import com.healthplus.healthplus_api.auth.domain.model.entity.User;
import com.healthplus.healthplus_api.auth.domain.model.enums.Role;
import com.healthplus.healthplus_api.auth.domain.persistance.UserRepository;
import com.healthplus.healthplus_api.auth.domain.service.UserService;
import com.healthplus.healthplus_api.auth.dto.*;
import com.healthplus.healthplus_api.auth.mapping.UserMapper;
import com.healthplus.healthplus_api.auth.security.PermissionService;
import com.healthplus.healthplus_api.auth.security.TokenProvider;
import com.healthplus.healthplus_api.auth.security.UserPrincipal;
import com.healthplus.healthplus_api.exception.BadRequestException;
import com.healthplus.healthplus_api.exception.ResourceNotFoundException;
import com.healthplus.healthplus_api.profile.domain.model.entity.Admin;
import com.healthplus.healthplus_api.profile.domain.model.entity.Specialist;
import com.healthplus.healthplus_api.profile.domain.persistance.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    @Transactional
    public LoginResponseDTO registerSpecialist(UserCreateDTO userCreateDTO) {

        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new BadRequestException("The email is already registered");
        }

        if (specialistRepository.existsByNamesAndLastNames(userCreateDTO.getSpecialistCreateDTO().getNames(),
                userCreateDTO.getSpecialistCreateDTO().getLastNames())) {
            throw new BadRequestException("The names and last names is already registered");
        }

        LoginDTO loginDTO = registerUserWithRole(userCreateDTO, Role.SPECIALIST);

        return login(loginDTO);
    }

    @Override
    @Transactional
    public LoginResponseDTO registerAdmin(UserCreateDTO userCreateDTO) {

        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new BadRequestException("The email is already registered");
        }

        LoginDTO loginDTO =  registerUserWithRole(userCreateDTO, Role.ADMIN);
        return login(loginDTO);
    }

    public LoginDTO registerUserWithRole(UserCreateDTO userCreateDTO, Role role) {

        String password = userCreateDTO.getPassword();
        userCreateDTO.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        User user = userMapper.toEntity(userCreateDTO);
        user.setRole(role);

        if (role == Role.SPECIALIST) {
            Specialist specialist = new Specialist();
            specialist.setNames(userCreateDTO.getSpecialistCreateDTO().getNames());
            specialist.setLastNames(userCreateDTO.getSpecialistCreateDTO().getLastNames());
            specialist.setBirthDate(userCreateDTO.getSpecialistCreateDTO().getBirthDate());
            specialist.setMedicalInstitution(userCreateDTO.getSpecialistCreateDTO().getMedicalInstitution());
            specialist.setSpecialty(userCreateDTO.getSpecialistCreateDTO().getSpecialty());
            specialist.setYearsOfExperience(userCreateDTO.getSpecialistCreateDTO().getYearsOfExperience());
            specialist.setCreatedAt(LocalDateTime.now());
            specialist.setUser(user);
            user.setSpecialist(specialist);
        } else if (role == Role.ADMIN) {
            Admin admin = new Admin();
            admin.setNames(userCreateDTO.getAdminCreateDTO().getNames());
            admin.setLastNames(userCreateDTO.getAdminCreateDTO().getLastNames());
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUser(user);
            user.setAdmin(admin);
        } else {
            throw new IllegalArgumentException("Role not valid");
        }

        User createdUser = userRepository.save(user);
        return new LoginDTO(createdUser.getEmail(), password);
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginDTO loginDTO) {

        try {
            // Autenticar al usuario utilizando AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
                    // Activa la lógica del CustomUserDetailsService (usa el userDetails de loadUserByUsername)
            );

            // Una vez autenticado, el objeto authentication contiene la información del usuario autenticado
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accessToken = tokenProvider.createAccessToken(authentication);
            //String refreshToken = tokenProvider.createRefreshToken(authentication, loginDTO.isRememberMe());
            String refreshToken = tokenProvider.createRefreshToken(authentication);

            return new LoginResponseDTO(userPrincipal.getUserByRoleId(), userPrincipal.getRole(), accessToken, refreshToken);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Incorrect password");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccessTokenResponseDTO refresh(String refreshToken) {

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String accessToken = tokenProvider.createAccessTokenFromRefreshToken(refreshToken);
        return new AccessTokenResponseDTO(accessToken);
    }

    @Override
    @Transactional
    public Boolean changePassword(Long specialistId, ChangePasswordDTO changePasswordDTO) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + specialistId));

        User user = specialist.getUser();

        if (!permissionService.hasSpecialistPermissionToAccess(specialist.getId())) {
            throw new AccessDeniedException("You do not have permission to change the password");
        }

        // Decodifica la contraseña almacenada y luego compara
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("The password entered does not match the registered password");
        }

        if(passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("The new password cannot be the same as the current one");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        this.userRepository.save(user);

        return true;
    }
}
