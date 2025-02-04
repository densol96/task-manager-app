package com.accenture.backend.model;

import com.accenture.backend.dto.LoginDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class SecurityUser implements UserDetails {

    private LoginDto loginDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(loginDto.getRole());
    }

    @Override
    public String getPassword() {
        return loginDto.getPassword();
    }

    @Override
    public String getUsername() {
        return loginDto.getEmail();
    }
}
