package com.accenture.backend.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PublicProjectDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private OwnerShortDto owner;
    private boolean member;
    private boolean hasPendingRequest;
}
