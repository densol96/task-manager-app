package com.accenture.backend.controller;

import com.accenture.backend.dto.request.AcceptProjectDto;
import com.accenture.backend.dto.request.InvitationDto;
import com.accenture.backend.dto.response.*;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private PublicProjectDto publicProjectDto;
    private BasicMessageDto basicMessageDto;

    @BeforeEach
    void setUp() {
        OwnerShortDto ownerShortDto = new OwnerShortDto(1L, 2L, "John Doe", "johndoe@example.com", "avatar.png");
        publicProjectDto = new PublicProjectDto(1L, "Test Project", "Description",
                LocalDateTime.now(), LocalDateTime.now(),
                ownerShortDto, true, false,
                ProjectMember.Role.OWNER);
        basicMessageDto = new BasicMessageDto("Operation successful");
    }

    @Test
    void testGetPublicProjects() {
        Page<PublicProjectDto> page = new PageImpl<>(List.of(publicProjectDto));
        when(projectService.getPublicProjects(any(), any(), any(), any())).thenReturn(page);

        ResponseEntity<Page<PublicProjectDto>> response = projectController.getPublicProjects(0, 5, "createdAt", "desc");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);
    }

    @Test
    void testGetProjectInfo() {
        when(projectService.getProjectInfo(eq(1L))).thenReturn(publicProjectDto);

        ResponseEntity<PublicProjectDto> response = projectController.getProjectInfo(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testCreateNewProject() {
        AcceptProjectDto dto = new AcceptProjectDto();
        BasicNestedResponseDto<ProjectDto> responseDto = new BasicNestedResponseDto<>("Project created", new ProjectDto());
        when(projectService.createNewProject(any())).thenReturn(responseDto);

        ResponseEntity<BasicNestedResponseDto<ProjectDto>> response = projectController.createNewProject(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testUpdateExistingProject() {
        AcceptProjectDto dto = new AcceptProjectDto();
        BasicNestedResponseDto<ProjectDto> responseDto = new BasicNestedResponseDto<>("Project updated", new ProjectDto());
        when(projectService.updateExistingProject(eq(1L), any())).thenReturn(responseDto);

        ResponseEntity<BasicNestedResponseDto<ProjectDto>> response = projectController.updateExistingProject(1L, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testDeleteProject() {
        when(projectService.deleteProject(eq(1L))).thenReturn(basicMessageDto);

        ResponseEntity<BasicMessageDto> response = projectController.deleteProject(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testMakeProjectInvitation() {
        InvitationDto dto = new InvitationDto();
        when(projectService.makeProjectInvitation(eq(1L), any())).thenReturn(basicMessageDto);

        ResponseEntity<BasicMessageDto> response = projectController.makeProjectInvitation(1L, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testAcceptInvitation() {
        when(projectService.acceptInvitation(eq(1L))).thenReturn(basicMessageDto);

        ResponseEntity<BasicMessageDto> response = projectController.acceptInvitation(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testDeclineInvitation() {
        when(projectService.declineInvitation(eq(1L))).thenReturn(basicMessageDto);

        ResponseEntity<BasicMessageDto> response = projectController.declineInvitation(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testCancelInvitation() {
        when(projectService.cancelInvitation(eq(1L))).thenReturn(basicMessageDto);

        ResponseEntity<BasicMessageDto> response = projectController.cancelInvitation(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testAcceptApplication() {
        when(projectService.acceptApplication(eq(1L))).thenReturn(basicMessageDto);

        ResponseEntity<BasicMessageDto> response = projectController.acceptApplication(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testDeclineApplication() {
        when(projectService.declineApplication(eq(1L))).thenReturn(basicMessageDto);

        ResponseEntity<BasicMessageDto> response = projectController.declineApplication(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetProjectInvitations() {
        List<UserInteractionDto> invitations = List.of(new UserInteractionDto());
        when(projectService.getProjectInvitations(eq(1L))).thenReturn(invitations);

        ResponseEntity<List<UserInteractionDto>> response = projectController.getProjectInvitations(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);
    }

    @Test
    void testGetUserInvitations() {
        List<ProjectInteractionDto> invitations = List.of(new ProjectInteractionDto());
        when(projectService.getUserInvitations()).thenReturn(invitations);

        ResponseEntity<List<ProjectInteractionDto>> response = projectController.getUserInvitations();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);
    }
}