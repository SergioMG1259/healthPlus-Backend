package com.healthplus.healthplus_api.auth.security;

import com.healthplus.healthplus_api.auth.domain.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long userByRoleId;
    private Long userId;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    //private Long userByRoleId; // id del specialist, admin, etc
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
