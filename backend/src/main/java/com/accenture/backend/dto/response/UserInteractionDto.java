package com.accenture.backend.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserInteractionDto {
    private Long id;
    private UserShortDto user;
    private LocalDateTime initAt;
}