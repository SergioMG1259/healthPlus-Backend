package com.healthplus.healthplus_api.auth.api;

import com.healthplus.healthplus_api.auth.domain.service.UserService;
import com.healthplus.healthplus_api.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register/specialist")
    public ResponseEntity<AuthResponseDTO> addUserSpecialist(@Valid @RequestBody UserCreateDTO userCreateDTO,
                                                             HttpServletResponse response) {

        LoginResponseDTO loginResponseDTO = userService.registerSpecialist(userCreateDTO);

        addRefreshTokenCookie(response, loginResponseDTO.getRefreshToken());

        return new ResponseEntity<AuthResponseDTO>(new AuthResponseDTO(loginResponseDTO.getUserRoleId(),
                loginResponseDTO.getRole(), loginResponseDTO.getAccessToken()), HttpStatus.OK);
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponseDTO> addUserAdmin(@Valid @RequestBody UserCreateDTO userCreateDTO,
                                                        HttpServletResponse response) {

        LoginResponseDTO loginResponseDTO = userService.registerSpecialist(userCreateDTO);

        addRefreshTokenCookie(response, loginResponseDTO.getRefreshToken());

        return new ResponseEntity<AuthResponseDTO>(new AuthResponseDTO(loginResponseDTO.getUserRoleId(),
                loginResponseDTO.getRole(), loginResponseDTO.getAccessToken()), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {

        LoginResponseDTO loginResponseDTO = userService.login(loginDTO);

        addRefreshTokenCookie(response, loginResponseDTO.getRefreshToken());

        return new ResponseEntity<AuthResponseDTO>(new AuthResponseDTO(loginResponseDTO.getUserRoleId(),
                loginResponseDTO.getRole(), loginResponseDTO.getAccessToken()), HttpStatus.OK);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        Cookie refreshTokenCookie = new Cookie("refresh_token_health_plus", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // Protege contra XSS
        refreshTokenCookie.setSecure(true); // Solo en HTTPS
        //refreshTokenCookie.setDomain("localhost");
        refreshTokenCookie.setPath("/api/v1/auth/refresh"); // Solo se enviará a esta ruta
        refreshTokenCookie.setAttribute("SameSite", "none");
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60); // 15 días

        response.addCookie(refreshTokenCookie);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponseDTO> refresh(HttpServletRequest request) {
        // Obtener la cookie
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token_health_plus".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh token cookie not found");
        }

        // Generar un nuevo access token usando el refresh token
        AccessTokenResponseDTO newAccessToken = userService.refresh(refreshToken);

        return new ResponseEntity<AccessTokenResponseDTO>(newAccessToken, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        Cookie refreshTokenCookie = new Cookie("refresh_token_health_plus", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/api/v1/auth/refresh");
        refreshTokenCookie.setAttribute("SameSite", "none");
        refreshTokenCookie.setMaxAge(0); // Expira inmediatamente
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SPECIALIST')")
    @PostMapping("/changePassword/specialist/{specialistId}")
    public ResponseEntity<Boolean> changePassword(@PathVariable Long specialistId, @RequestBody ChangePasswordDTO changePasswordDTO) {

        Boolean changed = userService.changePassword(specialistId, changePasswordDTO);
        return new ResponseEntity<Boolean>(changed, HttpStatus.OK);
    }
}
