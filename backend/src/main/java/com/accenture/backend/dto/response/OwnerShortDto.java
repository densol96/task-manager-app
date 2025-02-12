package com.accenture.backend.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OwnerShortDto {
        private Long userId;
        private Long memberId;
        private String firstName;
        private String lastName;
        private String email;
}