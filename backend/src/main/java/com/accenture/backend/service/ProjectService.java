package com.accenture.backend.service;

import org.springframework.data.domain.Page;

import com.accenture.backend.dto.response.OwnerShortDto;
import com.accenture.backend.dto.response.ProjectDto;

public interface ProjectService {
    Page<ProjectDto> getPublicProjects(Integer page, Integer size, String sortBy, String sortDirection);

    OwnerShortDto getProjectOwner(Long ownerId);

    Page<ProjectDto> getUserProjects(Integer page, Integer size, String sortBy, String sortDirection);
}
