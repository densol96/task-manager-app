package com.accenture.backend.dto.response;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NotificationShortDto {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private Boolean hasBeenRead;
}
