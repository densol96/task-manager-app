package com.accenture.backend.dto.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordDto {
    @NonNull
    @Size(min = 8, max = 50)
    private String oldPassword;

    @NonNull
    @Size(min = 8, max = 50)
    private String newPassword;
}
