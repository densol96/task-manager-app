package com.accenture.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.backend.dto.response.ProjectDto;
import com.accenture.backend.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/public")
    public ResponseEntity<Page<ProjectDto>> getPublicProjects(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return new ResponseEntity<>(projectService.getPublicProjects(page, size, sortBy, sortDirection), HttpStatus.OK);
    }

    @GetMapping("/public/{}")
    public ResponseEntity<Page<ProjectDto>> getUserProjects(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return new ResponseEntity<>(projectService.getUserProjects(page, size, sortBy, sortDirection), HttpStatus.OK);
    }
}
