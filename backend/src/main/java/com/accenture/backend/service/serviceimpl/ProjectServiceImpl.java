package com.accenture.backend.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.*;

import com.accenture.backend.dto.request.AcceptProjectDto;
import com.accenture.backend.dto.request.CommentDto;
import com.accenture.backend.dto.request.InvitationDto;
import com.accenture.backend.dto.response.*;
import com.accenture.backend.repository.*;
import com.accenture.backend.service.ProjectService;
import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;

import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectConfiguration;
import com.accenture.backend.entity.ProjectInteraction;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.User;
import com.accenture.backend.exception.custom.AlreadyExistsException;
import com.accenture.backend.exception.custom.EntityNotFoundException;
import com.accenture.backend.exception.custom.ForbiddenException;
import com.accenture.backend.exception.custom.InvalidInputException;
import com.accenture.backend.exception.custom.InvalidInteractionException;
import com.accenture.backend.exception.custom.MaxParticipantsReachedException;
import com.accenture.backend.exception.custom.MaxProjectOwnerLimitExceededException;
import com.accenture.backend.exception.custom.PageOutOfRangeException;
import com.accenture.backend.exception.custom.ServiceUnavailableException;
import com.accenture.backend.exception.custom.UserAlreadyMemberException;
import com.accenture.backend.enums.ProjectSortBy;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final ProjectMemberRepository projectMemberRepo;
    private final ProjectConfigurationRepository configRepo;
    private final UserRepository userRepository;
    private final ProjectInteractionRepository interactionRepo;

    @Value("${app.projects.max-amount}")
    private Integer maxProjectAmountAllowed;

    @Override
    public Page<PublicProjectDto> getPublicProjects(Integer page, Integer size, String sortBy, String sortDirection) {
        Long resultsTotal = projectRepo.countAllByConfigIsPublicTrue();
        Pageable pageable = validatePageableInput(page, size, resultsTotal, sortBy, sortDirection);
        Page<Project> projects = projectRepo.findAllByConfigIsPublicTrue(pageable);
        return projects.map(project -> maperToDto(project));
    }

    @Override
    public Page<PublicProjectDto> getUserProjects(Integer page, Integer size, String sortBy, String sortDirection) {
        Long userIdPlaceholder = 1L;
        Long resultsTotal = projectMemberRepo.countAllByUserId(userIdPlaceholder);
        Pageable pageable = validatePageableInput(page, size, resultsTotal, sortBy, sortDirection);
        /*
         * findProjectsByUserId uses a join with projectMembers via JPQL => above check
         * is acceptable
         */
        Page<Project> projects = projectRepo.findProjectsByUserId(userIdPlaceholder, pageable);
        return projects.map(project -> maperToDto(project));
    }

    @Override
    @Transactional
    public BasicNestedResponseDto<ProjectDto> createNewProject(AcceptProjectDto dto) {
        // USER PLACHOLDER
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));

        if (projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUser.getId(),
                ProjectMember.Role.OWNER) >= maxProjectAmountAllowed)
            throw new MaxProjectOwnerLimitExceededException();

        Project newProject = Project.builder().title(dto.getTitle()).description(dto.getDescription()).build();
        projectRepo.save(newProject);

        ProjectConfiguration config = ProjectConfiguration.builder().project(newProject).isPublic(dto.getIsPublic())
                .maxParticipants(dto.getMaxParticipants()).backgroundImage(null).build();
        configRepo.save(config);

        projectMemberRepo.save(ProjectMember.builder().user(loggedInUser).project(newProject)
                .projectRole(ProjectMember.Role.OWNER).build());

        PublicProjectDto generalInfo = maperToDto(newProject);
        ConfigDto configInfo = ConfigDto.builder().id(config.getId()).isPublic(config.getIsPublic())
                .maxParticipants(config.getMaxParticipants()).build();

        return new BasicNestedResponseDto<ProjectDto>(
                "New project has been succefully created",
                ProjectDto.builder().projectInfo(generalInfo).config(configInfo).build());
    }

    @Override
    @Transactional
    public BasicNestedResponseDto<ProjectDto> updateExistingProject(Long projectId, AcceptProjectDto dto) {
        Project existingProject = validateOwnershipAndReturnProject(projectId);

        existingProject.setTitle(dto.getTitle());
        existingProject.setDescription(dto.getDescription());

        ProjectConfiguration existingConfig = existingProject.getConfig();
        existingConfig.setIsPublic(dto.getIsPublic());
        existingConfig.setMaxParticipants(dto.getMaxParticipants());

        projectRepo.save(existingProject); // cascade active

        PublicProjectDto generalInfo = maperToDto(existingProject);
        ConfigDto configInfo = ConfigDto.builder().id(existingConfig.getId()).isPublic(existingConfig.getIsPublic())
                .maxParticipants(existingConfig.getMaxParticipants()).build();

        return new BasicNestedResponseDto<ProjectDto>(
                "Existing project has been succefully updated",
                ProjectDto.builder().projectInfo(generalInfo).config(configInfo).build());
    }

    @Override
    @Transactional
    public BasicMessageDto deleteProject(Long projectId) {
        Project existingProject = validateOwnershipAndReturnProject(projectId);
        projectRepo.delete(existingProject);
        return new BasicMessageDto("Project has been succesfully deleted");
    }

    @Override
    public BasicMessageDto makeProjectApplication(Long projectId, CommentDto dto) {
        // User Placeholder
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project", projectId));

        if (!project.getConfig().getIsPublic())
            throw new ForbiddenException("Users are not allowed to send applications to private projects.");

        if (projectMemberRepo.existsByUserIdAndProjectId(loggedInUser.getId(), projectId))
            throw new UserAlreadyMemberException("You are already a member of this project.");

        if (interactionRepo.existsByUserIdAndProjectIdAndStatus(loggedInUser.getId(), project.getId(),
                ProjectInteraction.Status.PENDING))
            throw new AlreadyExistsException("You already have a pending invitiation / application.");

        interactionRepo.save(
                ProjectInteraction.builder()
                        .user(loggedInUser)
                        .project(project)
                        .type(ProjectInteraction.Type.APPLICATION)
                        .status(ProjectInteraction.Status.PENDING)
                        .initComment(dto.getComment()).build());

        return new BasicMessageDto("Project application has been succesfully sent.");
    }

    @Override
    @Transactional
    public BasicMessageDto makeProjectInvitation(Long projectId, InvitationDto dto) {
        Project existingProject = validateOwnershipAndReturnProject(projectId);
        if (!(existingProject.getMembers().size() < existingProject.getConfig().getMaxParticipants()))
            throw new MaxParticipantsReachedException();

        User userToBeInvited = userRepository.findUserByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidInputException("No user exists with the email of " + dto.getEmail()));

        if (projectMemberRepo.existsByUserIdAndProjectId(userToBeInvited.getId(), projectId))
            throw new UserAlreadyMemberException("The user is already a member of this project.");

        if (interactionRepo.existsByUserIdAndProjectIdAndStatus(userToBeInvited.getId(), existingProject.getId(),
                ProjectInteraction.Status.PENDING))
            throw new AlreadyExistsException("User already has a pending invitiation / application.");

        interactionRepo.save(
                ProjectInteraction.builder()
                        .user(userToBeInvited)
                        .project(existingProject)
                        .type(ProjectInteraction.Type.INVITATION)
                        .status(ProjectInteraction.Status.PENDING)
                        .initComment(dto.getComment())
                        .build());

        return new BasicMessageDto("Project invitation has been succesfully sent.");
    }

    @Override
    public List<ProjectInteractionDto> getUserInvitations() {
        // User Placeholder
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));
        return interactionRepo
                .findAllByUserIdAndTypeAndStatus(loggedInUser.getId(), ProjectInteraction.Type.INVITATION,
                        ProjectInteraction.Status.PENDING)
                .stream()
                .map((interaction) -> {
                    Project project = interaction.getProject();
                    ProjectShortDto projectShortInfo = ProjectShortDto.builder().id(project.getId())
                            .title(project.getTitle()).build();
                    return ProjectInteractionDto.builder()
                            .project(projectShortInfo)
                            .initAt(interaction.getInitAt())
                            .comment(interaction.getInitComment()).build();
                }).toList();
    }

    @Override
    public List<ProjectInteractionDto> getUserApplications() {
        // User Placeholder
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));
        return interactionRepo
                .findAllByUserIdAndTypeAndStatus(loggedInUser.getId(), ProjectInteraction.Type.APPLICATION,
                        ProjectInteraction.Status.PENDING)
                .stream()
                .map((interaction) -> {
                    Project project = interaction.getProject();
                    ProjectShortDto projectShortInfo = ProjectShortDto.builder().id(project.getId())
                            .title(project.getTitle()).build();
                    return ProjectInteractionDto.builder()
                            .project(projectShortInfo)
                            .initAt(interaction.getInitAt())
                            .comment(interaction.getInitComment()).build();
                }).toList();
    }

    @Override
    public List<UserInteractionDto> getProjectInvitations(Long projectId) {
        validateOwnership(projectId);
        return interactionRepo
                .findAllByProjectIdAndTypeAndStatus(projectId, ProjectInteraction.Type.INVITATION,
                        ProjectInteraction.Status.PENDING)
                .stream()
                .map((interaction) -> {
                    User user = interaction.getUser();
                    UserShortDto userShortInfo = UserShortDto.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail()).build();
                    return UserInteractionDto.builder()
                            .user(userShortInfo)
                            .initAt(interaction.getInitAt())
                            .comment(interaction.getInitComment()).build();
                }).toList();
    }

    @Override
    public List<UserInteractionDto> getProjectApplications(Long projectId) {
        validateOwnership(projectId);
        return interactionRepo
                .findAllByProjectIdAndTypeAndStatus(projectId, ProjectInteraction.Type.APPLICATION,
                        ProjectInteraction.Status.PENDING)
                .stream()
                .map((interaction) -> {
                    User user = interaction.getUser();
                    UserShortDto userShortInfo = UserShortDto.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail()).build();
                    return UserInteractionDto.builder()
                            .user(userShortInfo)
                            .initAt(interaction.getInitAt())
                            .comment(interaction.getInitComment()).build();
                }).toList();
    }

    @Override
    public BasicMessageDto acceptApplication(Long applicationId) {
        // USER PLACHOLDER
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));

        ProjectInteraction application = validateAndReturnProjectInteraction(applicationId);

        validateActiveApplication(application);
        Project project = validateOwnershipAndReturnProject(application.getProject().getId());

        application.setResponseDate(LocalDateTime.now());
        application.setStatus(ProjectInteraction.Status.ACCEPTED);

        interactionRepo.save(application);

        /*
         * Removed Transactional cause if for whatever reason user has a pending
         * application / invitation and he is already a member (this should not happen,
         * but added an additional check just in case), then we still want to throw an
         * Exception before saving a new member, but that interaction that is pending
         * should be changed to accepted.
         */

        if (!projectMemberRepo.existsByUserIdAndProjectId(application.getUser().getId(), project.getId()))
            throw new UserAlreadyMemberException();

        projectMemberRepo
                .save(ProjectMember.builder().user(application.getUser())
                        .project(project)
                        .projectRole(ProjectMember.Role.USER).build());

        return new BasicMessageDto("User application to join the project has been accepted");
    }

    @Override
    public BasicMessageDto declineApplication(Long applicationId) {
        ProjectInteraction application = validateAndReturnProjectInteraction(applicationId);

        validateActiveApplication(application);
        validateOwnership(application.getProject().getId());

        application.setResponseDate(LocalDateTime.now());
        application.setStatus(ProjectInteraction.Status.DECLINED);

        interactionRepo.save(application);

        return new BasicMessageDto("User application to join the project has been declined");
    }

    @Override
    public BasicMessageDto acceptInvitation(Long invitationId) {
        // USER PLACHOLDER
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));

        ProjectInteraction invitation = validateAndReturnProjectInteraction(invitationId);

        validateActiveInvitationForUser(invitation, loggedInUser.getId());

        invitation.setResponseDate(LocalDateTime.now());
        invitation.setStatus(ProjectInteraction.Status.ACCEPTED);

        interactionRepo.save(invitation);

        Long userId = loggedInUser.getId();
        Project project = invitation.getProject();

        if (projectMemberRepo.existsByUserIdAndProjectId(userId, project.getId()))
            throw new UserAlreadyMemberException();

        projectMemberRepo.save(ProjectMember.builder().user(loggedInUser)
                .project(project)
                .projectRole(ProjectMember.Role.USER)
                .build());

        return new BasicMessageDto("Project invitation has succefully been accepted.");
    }

    @Override
    public BasicMessageDto declineInvitation(Long invitationId) {
        // USER PLACHOLDER
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));

        ProjectInteraction invitation = validateAndReturnProjectInteraction(invitationId);

        validateActiveInvitationForUser(invitation, loggedInUser.getId());

        invitation.setResponseDate(LocalDateTime.now());
        invitation.setStatus(ProjectInteraction.Status.DECLINED);

        interactionRepo.save(invitation);
        return new BasicMessageDto("Project invitation has succefully been declined.");
    }

    @Override
    public BasicMessageDto cancelApplication(Long applicationId) {
        // USER PLACHOLDER
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));
        ProjectInteraction application = validateAndReturnProjectInteraction(applicationId);

        validateActiveApplication(application);

        if (application.getUser().getId() != loggedInUser.getId())
            throw new ForbiddenException("Users are not allowed to manage other user's applications / invitations");

        interactionRepo.delete(application);

        return new BasicMessageDto("Project application has succefully been calceled.");

    }

    @Override
    public BasicMessageDto cancelInvitation(Long invitationId) {
        ProjectInteraction invitation = validateAndReturnProjectInteraction(invitationId);
        validateOwnership(invitation.getProject().getId());

        if (invitation.getType() != ProjectInteraction.Type.INVITATION)
            throw new InvalidInteractionException("This interaction is not an invitation.");

        if (invitation.getStatus() != ProjectInteraction.Status.PENDING)
            throw new InvalidInteractionException("This invitation has already been reviewed.");

        interactionRepo.delete(invitation);
        return new BasicMessageDto("Project invitation has been succefully canceled.");
    }

    private void validateActiveInvitationForUser(ProjectInteraction invitation, Long loggedinUserId) {
        if (invitation.getType() != ProjectInteraction.Type.INVITATION)
            throw new InvalidInteractionException("This interaction is not an invitation.");

        if (invitation.getStatus() != ProjectInteraction.Status.PENDING)
            throw new InvalidInteractionException("This invitation has already been reviewed.");

        if (invitation.getUser().getId() != loggedinUserId)
            throw new ForbiddenException("Users are not allowed to manage other user's applications / invitations");
    }

    private void validateActiveApplication(ProjectInteraction application) {
        if (application.getType() != ProjectInteraction.Type.APPLICATION)
            throw new InvalidInteractionException("This interaction is not an application.");

        if (application.getStatus() != ProjectInteraction.Status.PENDING)
            throw new InvalidInteractionException("This application has already been reviewew.");
    }

    private ProjectInteraction validateAndReturnProjectInteraction(Long interactionId) {
        if (interactionId < 1)
            throw new InvalidInputException("project application ID", interactionId);

        return interactionRepo.findById(interactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No project interaction found with the id of " + interactionId));
    }

    private Project validateOwnershipAndReturnProject(Long projectId) {
        return validateOwnership(projectId).getProject();
    }

    private ProjectMember validateOwnership(Long projectId) {
        if (projectId < 1)
            throw new InvalidInputException("project ID", projectId);

        // USER PLACHOLDER
        User loggedInUser = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the id of " + 1));

        ProjectMember currentProjectOwner = getProjectOwner(projectId);

        if (currentProjectOwner.getUser().getId() != loggedInUser.getId())
            throw new ForbiddenException("You are not the owner of this project and cannot perform this action.");

        return currentProjectOwner;
    }

    private ProjectMember getProjectOwner(Long projectId) {
        if (projectId <= 0)
            throw new InvalidInputException("project ID", projectId);

        if (!projectRepo.existsById(projectId))
            throw new EntityNotFoundException("Project", projectId);

        List<ProjectMember> owners = projectMemberRepo.findByProjectIdAndProjectRole(projectId,
                ProjectMember.Role.OWNER);
        /*
         * In the current implementation the is only 1 owner, but in case it will be
         * change to multi-ownership later, it makes sense to perform an additional
         * check and return the 1st owner (in our version, though, it is the only owner
         * as well).
         * 
         * If no owner found => This should not happen, however if, for whatever reason
         * it does happen, throw ServiceUnavailableException.
         * It will log actual exception's cause but return a general error message to
         * the user.
         */
        if (owners.size() == 0)
            throw new ServiceUnavailableException("No owner found for the project with the id of " + projectId);

        if (owners.size() > 1) {
            System.out.println("Multiple owners found for project " + projectId);
        }

        return owners.get(0);
    }

    private Pageable validatePageableInput(Integer page, Integer size, Long resultsTotal, String sortBy,
            String sortDirection) {
        if (page != null && page < 1)
            throw new InvalidInputException("page", page);
        if (size < 1)
            throw new InvalidInputException("size", size);

        long totalPages = (long) Math.ceil((double) resultsTotal / size);

        if (page != null && page > totalPages)
            throw new PageOutOfRangeException(page, totalPages);

        ProjectSortBy sortByEnum;
        try {
            sortByEnum = ProjectSortBy.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Projects can only be sorted by either date or title.");
        }
        Sort sort = sortDirection.toLowerCase().equals("desc") ? Sort.by(sortByEnum.fieldName()).descending()
                : Sort.by(sortByEnum.fieldName()).ascending();
        return page == null ? PageRequest.of(0, Integer.MAX_VALUE, sort) : PageRequest.of(page - 1, size, sort);
    }

    private PublicProjectDto maperToDto(Project project) {
        return PublicProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .owner(OwnerShortDto.fromEntity(getProjectOwner(project.getId()))).build();
    }
}
