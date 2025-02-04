package com.accenture.backend.dto.response;

import java.time.LocalDateTime;

public record ProjectDto(
                Long id,
                String title,
                String description,
                LocalDateTime createdAt,
                OwnerShortDto owner) {
}
