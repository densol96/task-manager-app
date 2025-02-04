package com.accenture.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.backend.dto.response.ProjectDto;
import com.accenture.backend.service.project.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService accountService;

    @GetMapping("/public")
    public ResponseEntity<List<ProjectDto>> displayBalance() {
        return new ResponseEntity<>(accountService.getAllPublicProjects(), HttpStatus.OK);
    }
}
