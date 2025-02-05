package com.accenture.backend.dto.response;

import java.time.LocalDateTime;

import com.accenture.backend.entity.ProjectInteraction.Status;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private UserShortDto user;
    private LocalDateTime initAt;
    private String comment;
}