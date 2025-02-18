package com.accenture.backend.dto.user;

import com.accenture.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleDto {
    private Long id;
    private String email;
    private String password;
    private Role role;
}
