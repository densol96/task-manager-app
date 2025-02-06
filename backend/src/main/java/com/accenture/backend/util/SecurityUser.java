package com.accenture.backend.util;

import com.accenture.backend.dto.user.UserRoleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class SecurityUser implements UserDetails {

    private UserRoleDto userRoleDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(userRoleDto.getRole());
    }

    @Override
    public String getPassword() {
        return userRoleDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userRoleDto.getEmail();
    }
}
