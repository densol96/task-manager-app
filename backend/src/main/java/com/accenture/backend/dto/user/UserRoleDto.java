package com.accenture.backend.dto.user;

import com.accenture.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDto {
    private String email;
    private String password;
    private Role role;
}
