package com.accenture.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.accenture.backend.dto.request.AcceptProjectDto;
import com.accenture.backend.dto.request.InvitationDto;
import com.accenture.backend.dto.response.UserInteractionDto;
import com.accenture.backend.dto.response.ProjectMemberInfoDto;
import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.BasicNestedResponseDto;
import com.accenture.backend.dto.response.ProjectInteractionDto;
import com.accenture.backend.dto.response.PublicProjectDto;
import com.accenture.backend.dto.response.ProjectDto;
import com.accenture.backend.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
        System.out.println(page);
        System.out.println(sortDirection);
        System.out.println(size);
        System.out.println(sortBy);
        return new ResponseEntity<>(projectService.getPublicProjects(page, size,
                sortBy, sortDirection), HttpStatus.OK);
    }

    @GetMapping("/owned")
    public ResponseEntity<Page<PublicProjectDto>> getUserProjects(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return new ResponseEntity<>(projectService.getUserProjects(page, size, sortBy, sortDirection), HttpStatus.OK);
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<Page<ProjectMemberInfoDto>> getProjectMembers(@PathVariable Long projectId,
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "all") String filterBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return new ResponseEntity<>(projectService.getProjectMembers(projectId, page, size, sortDirection),
                HttpStatus.OK);
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

    @DeleteMapping("/{projectId}/leave")
    public ResponseEntity<BasicMessageDto> leaveProject(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.leaveProject(projectId), HttpStatus.OK);
    }

    @DeleteMapping("/{projectMemberId}/kick")
    public ResponseEntity<BasicMessageDto> excludeFromProject(@PathVariable Long projectMemberId) {
        return new ResponseEntity<>(projectService.kickMemberOut(projectMemberId), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<PublicProjectDto> getProjectInfo(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getProjectInfo(projectId), HttpStatus.OK);
    }

    // ========== Invitation Endpoints ==========

    @PostMapping("/{projectId}/invitation")
    public ResponseEntity<BasicMessageDto> makeProjectInvitation(
            @PathVariable Long projectId, @Valid @RequestBody InvitationDto dto) {
        return new ResponseEntity<>(projectService.makeProjectInvitation(projectId, dto), HttpStatus.CREATED);
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<BasicMessageDto> acceptInvitation(
            @PathVariable Long invitationId) {
        return new ResponseEntity<>(projectService.acceptInvitation(invitationId), HttpStatus.CREATED);
    }

    @PostMapping("/invitations/{invitationId}/decline")
    public ResponseEntity<BasicMessageDto> declineInvitation(
            @PathVariable Long invitationId) {
        return new ResponseEntity<>(projectService.declineInvitation(invitationId), HttpStatus.CREATED);
    }

    @DeleteMapping("/invitations/{invitationId}")
    public ResponseEntity<BasicMessageDto> cancelInvitation(
            @PathVariable Long invitationId) {
        return new ResponseEntity<>(projectService.cancelInvitation(invitationId), HttpStatus.CREATED);
    }

    // ========== Application Endpoints ==========

    @PostMapping("/{projectId}/application")
    public ResponseEntity<BasicMessageDto> makeProjectApplication(
            @PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.makeProjectApplication(projectId), HttpStatus.CREATED);
    }

    @PostMapping("/applications/{applicationId}/accept")
    public ResponseEntity<BasicMessageDto> acceptApplication(
            @PathVariable Long applicationId) {
        return new ResponseEntity<>(projectService.acceptApplication(applicationId), HttpStatus.CREATED);
    }

    @PostMapping("/applications/{applicationId}/decline")
    public ResponseEntity<BasicMessageDto> declineApplication(
            @PathVariable Long applicationId) {
        return new ResponseEntity<>(projectService.declineApplication(applicationId), HttpStatus.CREATED);
    }

    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<BasicMessageDto> deleteApplication(
            @PathVariable Long applicationId) {
        return new ResponseEntity<>(projectService.cancelApplication(applicationId), HttpStatus.CREATED);
    }

    // ========== User Interaction Endpoints ==========

    @GetMapping("/invitations")
    public ResponseEntity<List<ProjectInteractionDto>> getUserInvitations() {
        return new ResponseEntity<>(projectService.getUserInvitations(), HttpStatus.OK);
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ProjectInteractionDto>> getUserApplications() {
        return new ResponseEntity<>(projectService.getUserApplications(), HttpStatus.OK);
    }

    @GetMapping("/{projectId}/invitations")
    public ResponseEntity<List<UserInteractionDto>> getProjectInvitations(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getProjectInvitations(projectId), HttpStatus.OK);
    }

    @GetMapping("/{projectId}/applications")
    public ResponseEntity<List<UserInteractionDto>> getProjectApplications(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getProjectApplications(projectId), HttpStatus.OK);
    }
}
