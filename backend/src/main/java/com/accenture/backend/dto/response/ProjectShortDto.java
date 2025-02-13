package com.accenture.backend.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProjectShortDto {
    private Long id;
    private String title;
}
