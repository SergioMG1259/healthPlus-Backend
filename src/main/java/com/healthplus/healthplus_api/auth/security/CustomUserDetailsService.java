package com.healthplus.healthplus_api.auth.security;

import com.healthplus.healthplus_api.auth.domain.model.entity.User;
import com.healthplus.healthplus_api.auth.domain.persistance.UserRepository;
import com.healthplus.healthplus_api.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Long userRoleId;

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Unregistered email"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toString());

        if (user.getSpecialist() != null) {
            userRoleId = user.getSpecialist().getId();
        } else if (user.getAdmin() != null) {
            userRoleId = user.getAdmin().getId();
        }else {
            throw new BadRequestException("The user does not have a role associated");
        }

        return new UserPrincipal(
                userRoleId,
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority),
                user.getRole().toString()
        );
    }
}
