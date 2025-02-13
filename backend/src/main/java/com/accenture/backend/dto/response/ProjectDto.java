package com.accenture.backend.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProjectDto {
    private PublicProjectDto projectInfo;
    private ConfigDto config;

    public ProjectDto() {
    }
}
