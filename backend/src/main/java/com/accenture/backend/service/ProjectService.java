package com.accenture.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.accenture.backend.dto.request.*;
import com.accenture.backend.dto.response.*;

public interface ProjectService {

    PublicProjectDto getProjectInfo(Long projectId);

    ProjectConfigDto getOwnerProjectInfo(Long projectId);

    Page<PublicProjectDto> getPublicProjects(Integer page, Integer size, String sortBy, String sortDirection);

    Page<PublicProjectDto> getUserProjects(Integer page, Integer size, String sortBy, String sortDirection);

    Page<ProjectMemberInfoDto> getProjectMembers(Long projectId, Integer page, Integer size, String sortDirection);

    BasicNestedResponseDto<ProjectDto> createNewProject(AcceptProjectDto dto);

    BasicNestedResponseDto<ProjectDto> updateExistingProject(Long projectId, AcceptProjectDto dto);

    BasicMessageDto deleteProject(Long projectId);

    BasicMessageDto makeProjectApplication(Long projectId);

    BasicMessageDto makeProjectInvitation(Long projectId, InvitationDto dto);

    BasicMessageDto acceptApplication(Long applicationId);

    BasicMessageDto declineApplication(Long applicationId);

    BasicMessageDto acceptInvitation(Long invitationId);

    BasicMessageDto declineInvitation(Long invitationId);

    BasicMessageDto cancelApplication(Long applicationId);

    BasicMessageDto cancelInvitation(Long invitationId);

    BasicMessageDto kickMemberOut(Long projectMemberId);

    BasicMessageDto leaveProject(Long projectId);

    List<ProjectInteractionDto> getUserInvitations();

    List<ProjectInteractionDto> getUserApplications();

    List<UserInteractionDto> getProjectApplications(Long projectId);

    List<UserInteractionDto> getProjectInvitations(Long projectId);
}
