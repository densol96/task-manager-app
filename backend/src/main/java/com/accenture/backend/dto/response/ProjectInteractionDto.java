package com.accenture.backend.dto.response;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProjectInteractionDto {
    private Long id;
    private ProjectShortDto project;
    private OwnerShortDto owner;
    private LocalDateTime initAt;
    private String comment;
}
