package com.accenture.backend.dto;

import com.accenture.backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class UserInfoDto {

    @Email
    @NonNull
    private String email;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;

    @NonNull
    @Size(min = 8, max = 50)
    private String password;

    private static Role role = Role.USER;
}
