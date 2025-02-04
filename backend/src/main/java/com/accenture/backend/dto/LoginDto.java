package com.accenture.backend.dto;

import com.accenture.backend.model.Role;
import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
    private Role role;
}
