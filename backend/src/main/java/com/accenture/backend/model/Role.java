package com.accenture.backend.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, MODERATOR, ADMIN, NOT_CONFIRMED;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
