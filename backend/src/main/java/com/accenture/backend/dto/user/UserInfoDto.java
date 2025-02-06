package com.accenture.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
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
}
