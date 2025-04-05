package com.healthplus.healthplus_api.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PermissionService {

    public boolean hasSpecialistPermissionToAccess(Long specialistId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Obtener el usuario autenticado
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        // Obtener el rol de ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // Validación de permiso: El usuario debe ser el especialista asociado o un administrador
        return Objects.equals(specialistId, userPrincipal.getUserByRoleId()) || isAdmin;
    }
}
