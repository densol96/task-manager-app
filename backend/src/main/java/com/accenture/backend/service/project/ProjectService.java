package com.accenture.backend.service.project;

import org.springframework.data.domain.Page;

import com.accenture.backend.dto.request.*;
import com.accenture.backend.dto.response.*;

public interface ProjectService {
    Page<PublicProjectDto> getPublicProjects(Integer page, Integer size, String sortBy, String sortDirection);

    Page<PublicProjectDto> getUserProjects(Integer page, Integer size, String sortBy, String sortDirection);

    BasicNestedResponseDto<ProjectDto> createNewProject(AcceptProjectDto dto);

    BasicNestedResponseDto<ProjectDto> updateExistingProject(Long projectId, AcceptProjectDto dto);

    BasicMessageDto deleteProject(Long projectId);
}
