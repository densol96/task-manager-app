package com.accenture.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.backend.dto.request.AcceptProjectDto;
import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.BasicNestedResponseDto;
import com.accenture.backend.dto.response.ProjectDto;
import com.accenture.backend.dto.response.PublicProjectDto;
import com.accenture.backend.service.project.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/public")
    public ResponseEntity<Page<PublicProjectDto>> getPublicProjects(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return new ResponseEntity<>(projectService.getPublicProjects(page, size, sortBy, sortDirection), HttpStatus.OK);
    }

    @GetMapping("/owned")
    public ResponseEntity<Page<PublicProjectDto>> getUserProjects(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return new ResponseEntity<>(projectService.getUserProjects(page, size, sortBy, sortDirection), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BasicNestedResponseDto<ProjectDto>> createNewProject(
            @Valid @RequestBody AcceptProjectDto dto) {
        return new ResponseEntity<>(projectService.createNewProject(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<BasicNestedResponseDto<ProjectDto>> updateExistingProject(@PathVariable Long projectId,
            @Valid @RequestBody AcceptProjectDto dto) {
        return new ResponseEntity<>(projectService.updateExistingProject(projectId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<BasicMessageDto> deleteProject(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.deleteProject(projectId), HttpStatus.OK);
    }

}
