package com.accenture.backend.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ConfigDto {
    private Long id;
    private Boolean isPublic;
    private Integer maxParticipants;
}
