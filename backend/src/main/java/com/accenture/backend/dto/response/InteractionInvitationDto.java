package com.accenture.backend.dto.response;

import java.time.LocalDateTime;

import com.accenture.backend.entity.ProjectInteraction.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class InteractionInvitationDto {
    private ProjectShortDto project;
    private LocalDateTime initAt;
    private String comment;
}
