package com.accenture.backend.service.project;

import java.util.List;

import com.accenture.backend.dto.response.OwnerShortDto;
import com.accenture.backend.dto.response.ProjectDto;

public interface ProjectService {
    List<ProjectDto> getAllPublicProjects();

    OwnerShortDto getProjectOwner(Long ownerId);
}
