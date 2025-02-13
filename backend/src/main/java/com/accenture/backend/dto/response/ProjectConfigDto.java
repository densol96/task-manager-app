package com.accenture.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProjectConfigDto {
    private Long configId;
    private Long projectId;
    private Boolean isPublic;
    private Integer maxParticipants;

    public ProjectConfigDto() {
    }
}
