package com.accenture.backend.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserShortDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}