package com.accenture.backend.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import com.accenture.backend.dto.request.AcceptProjectDto;
import com.accenture.backend.dto.request.InvitationDto;
import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.BasicNestedResponseDto;
import com.accenture.backend.dto.response.ConfigDto;
import com.accenture.backend.dto.response.OwnerShortDto;
import com.accenture.backend.dto.response.ProjectConfigDto;
import com.accenture.backend.dto.response.ProjectDto;
import com.accenture.backend.dto.response.ProjectInteractionDto;
import com.accenture.backend.dto.response.ProjectShortDto;
import com.accenture.backend.dto.response.PublicProjectDto;
import com.accenture.backend.dto.response.UserInteractionDto;
import com.accenture.backend.dto.response.ProjectMemberInfoDto;
import com.accenture.backend.dto.response.UserShortDto;

import com.accenture.backend.service.PremiumAccountService;
import com.accenture.backend.service.ProjectService;
import com.accenture.backend.service.UserService;

import com.accenture.backend.entity.Notification;
import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectConfiguration;
import com.accenture.backend.entity.ProjectInteraction;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.User;

import com.accenture.backend.exception.AlreadyExistsException;
import com.accenture.backend.exception.AuthenticationRuntimeException;
import com.accenture.backend.exception.EntityNotFoundException;
import com.accenture.backend.exception.ForbiddenException;
import com.accenture.backend.exception.InvalidInputException;
import com.accenture.backend.exception.InvalidInteractionException;
import com.accenture.backend.exception.MaxParticipantsReachedException;
import com.accenture.backend.exception.MaxProjectOwnerLimitExceededException;
import com.accenture.backend.exception.UserAlreadyMemberException;

import com.accenture.backend.repository.NotificationRepository;
import com.accenture.backend.repository.ProjectConfigurationRepository;
import com.accenture.backend.repository.ProjectInteractionRepository;
import com.accenture.backend.repository.ProjectMemberRepository;
import com.accenture.backend.repository.ProjectRepository;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.enums.ProjectSortBy;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final ProjectMemberRepository projectMemberRepo;
    private final ProjectConfigurationRepository configRepo;
    private final UserRepository userRepository;
    private final ProjectInteractionRepository interactionRepo;
    private final NotificationRepository notificationRepo;
    private final UserService userService;
    private final PremiumAccountService premiumAccountService;

    @Value("${app.projects.max-amount}")
    private Integer maxProjectAmountAllowed;

    @Value("${app.projects.max-amount-with-premium}")
    private Integer maxProjectAmountAllowedWithPremium;

    @Override
    public PublicProjectDto getProjectInfo(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new InvalidInputException("project ID", projectId));
        ProjectConfiguration config = getProjectConfig(project);
        Long loggedInUserId = userService.getLoggedInUserId();

        if (config.getIsPublic() == false && !projectMemberRepo.existsByUserIdAndProjectId(loggedInUserId, projectId))
            throw new ForbiddenException("You cannot see the private project's information unless you are a member.");

        return projectToPublicProjectDto(project, null);
    }

    @Override
    public ProjectConfigDto getOwnerProjectInfo(Long projectId) {
        validateThatLoggedInUserIsOwner(projectId);
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project", projectId));
        ProjectConfiguration config = getProjectConfig(project);

        return ProjectConfigDto.builder()
                .configId(config.getId())
                .projectId(projectId)
                .maxParticipants(config.getMaxParticipants())
                .isPublic(config.getIsPublic())
                .build();
    }

    @Override
    public Page<PublicProjectDto> getPublicProjects(Integer page, Integer size, String sortBy, String sortDirection) {
        Long resultsTotal = projectRepo.countAllByConfigIsPublicTrue();
        if (resultsTotal == 0)
            return Page.empty();

        Pageable pageable = validatePageableInput(page, size, resultsTotal, parseProjectSortBy(sortBy).fieldName(),
                sortDirection);
        Page<Project> projects = projectRepo.findAllByConfigIsPublicTrue(pageable);
        return projects.map(project -> projectToPublicProjectDto(project, null));
    }

    @Override
    public Page<PublicProjectDto> getUserProjects(Integer page, Integer size, String sortBy, String sortDirection) {
        Long loggedInUserId = userService.getLoggedInUserId();
        Long resultsTotal = projectMemberRepo.countAllByUserId(loggedInUserId);
        if (resultsTotal == 0)
            return Page.empty();

        Pageable pageable = validatePageableInput(page, size, resultsTotal, parseProjectSortBy(sortBy).fieldName(),
                sortDirection);
        Page<Project> projects = projectRepo.findProjectsByUserId(loggedInUserId, pageable);
        return projects.map(project -> projectToPublicProjectDto(project, null));
    }

    @Override
    public Page<ProjectMemberInfoDto> getProjectMembers(Long projectId, Integer page, Integer size,
            String sortDirection) {
        Long loggedInUserId = userService.getLoggedInUserId();
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project", projectId));

        ProjectConfiguration config = getProjectConfig(project);

        if (config.getIsPublic() == false && !projectMemberRepo.existsByUserIdAndProjectId(loggedInUserId, projectId))
            throw new ForbiddenException("Only members of the project can perform this action");

        Long resultsTotal = projectMemberRepo.countAllByProjectId(projectId);
        if (resultsTotal == 0)
            return Page.empty();

        String sortField = "joinDate";
        Pageable pageable = validatePageableInput(page, size, resultsTotal, sortField, sortDirection);
        Page<ProjectMember> projects = projectMemberRepo.findByProjectId(projectId, pageable);
        return projects.map(this::projectMemberToUserPublicInfoDto);
    }

    @Override
    @Transactional
    public BasicNestedResponseDto<ProjectDto> createNewProject(AcceptProjectDto dto) {
        Long loggedInUserId = userService.getLoggedInUserId();

        boolean hasPremiumAccount = premiumAccountService.userHasActivePremiumAccount(loggedInUserId);
        Long amountAccountsOwned = projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId,
                ProjectMember.Role.OWNER);

        if (hasPremiumAccount && amountAccountsOwned >= maxProjectAmountAllowedWithPremium)
            throw new MaxProjectOwnerLimitExceededException(
                    "You have already reached your 10 accounts per premium account limit.");

        if (!hasPremiumAccount && amountAccountsOwned >= maxProjectAmountAllowed)
            throw new MaxProjectOwnerLimitExceededException();

        Project newProject = projectRepo
                .save(Project.builder().title(dto.getTitle()).description(dto.getDescription()).build());

        ProjectConfiguration config = configRepo.save(ProjectConfiguration.builder()
                .project(newProject)
                .isPublic(dto.getIsPublic())
                .maxParticipants(dto.getMaxParticipants())
                .build());

        ProjectMember owner = projectMemberRepo.save(ProjectMember.builder()
                .user(userRepository.findById(loggedInUserId).orElseThrow(() -> new AuthenticationRuntimeException()))
                .project(newProject)
                .projectRole(ProjectMember.Role.OWNER)
                .build());

        PublicProjectDto generalInfo = projectToPublicProjectDto(newProject, owner);
        ConfigDto configInfo = ConfigDto.builder()
                .id(config.getId())
                .isPublic(config.getIsPublic())
                .maxParticipants(config.getMaxParticipants())
                .build();

        return new BasicNestedResponseDto<ProjectDto>(
                "New project has been successfully created",
                ProjectDto.builder().projectInfo(generalInfo).config(configInfo).build());
    }

    @Override
    @Transactional
    public BasicNestedResponseDto<ProjectDto> updateExistingProject(Long projectId, AcceptProjectDto dto) {
        Project existingProject = returnProjectForOwner(projectId);

        existingProject.setTitle(dto.getTitle());
        existingProject.setDescription(dto.getDescription());

        ProjectConfiguration existingConfig = getProjectConfig(existingProject);

        existingConfig.setIsPublic(dto.getIsPublic());
        existingConfig.setMaxParticipants(dto.getMaxParticipants());

        projectRepo.save(existingProject); // cascade active

        PublicProjectDto generalInfo = projectToPublicProjectDto(existingProject, null);
        ConfigDto configInfo = ConfigDto.builder()
                .id(existingConfig.getId())
                .isPublic(existingConfig.getIsPublic())
                .maxParticipants(existingConfig.getMaxParticipants()).build();

        return new BasicNestedResponseDto<ProjectDto>(
                "Existing project has been succefully updated",
                ProjectDto.builder().projectInfo(generalInfo).config(configInfo).build());
    }

    @Override
    @Transactional
    public BasicMessageDto deleteProject(Long projectId) {
        Project project = returnProjectForOwner(projectId);
        List<User> users = projectMemberRepo.findUsersByProjectId(projectId);
        projectRepo.deleteById(projectId);

        String message = project.getTitle() + " #" + project.getId() + " has been deleted by its owner.";
        for (User user : users) {
            notificationRepo
                    .save(Notification.builder()
                            .title("Project has been deleted")
                            .message(message)
                            .user(user)
                            .build());
        }

        return new BasicMessageDto("Project has been succesfully deleted");
    }

    @Override
    @Transactional
    public BasicMessageDto makeProjectApplication(Long projectId) {
        if (!projectRepo.existsByIdAndConfigIsPublicTrue(projectId))
            throw new ForbiddenException("Users are not allowed to send applications to private projects.");

        Long loggedInUserId = userService.getLoggedInUserId();

        if (projectMemberRepo.existsByUserIdAndProjectId(loggedInUserId, projectId))
            throw new UserAlreadyMemberException("You are already a member of this project.");

        if (interactionRepo.existsByUserIdAndProjectIdAndStatus(loggedInUserId, projectId,
                ProjectInteraction.Status.PENDING))
            throw new AlreadyExistsException("You already have a pending invitiation / application.");

        User whoIsMakingApplication = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new AuthenticationRuntimeException());
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("No project found with the id of " + projectId));

        ProjectInteraction newApplication = ProjectInteraction.builder()
                .user(whoIsMakingApplication)
                .project(project)
                .type(ProjectInteraction.Type.APPLICATION)
                .status(ProjectInteraction.Status.PENDING)
                .build();

        interactionRepo.save(newApplication);

        ProjectMember owner = getProjectOwner(projectId);
        String messsage = whoIsMakingApplication.getEmail() + "has applied to join your project " + project.getTitle()
                + "#" + projectId;

        notificationRepo
                .save(Notification.builder()
                        .title("New project application")
                        .message(messsage)
                        .user(owner.getUser())
                        .build());

        return new BasicMessageDto("Project application has been succesfully sent.");
    }

    @Override
    @Transactional
    public BasicMessageDto makeProjectInvitation(Long projectId, InvitationDto dto) {
        Project existingProject = returnProjectForOwner(projectId);
        ProjectConfiguration existingConfig = getProjectConfig(existingProject);
        if (existingProject.getMembers().size() >= existingConfig.getMaxParticipants())
            throw new MaxParticipantsReachedException();

        User userToBeInvited = userRepository.findUserByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidInputException("No user exists with the email of " + dto.getEmail()));

        if (projectMemberRepo.existsByUserIdAndProjectId(userToBeInvited.getId(), projectId))
            throw new UserAlreadyMemberException("The user is already a member of this project.");

        if (interactionRepo.existsByUserIdAndProjectIdAndStatus(userToBeInvited.getId(), existingProject.getId(),
                ProjectInteraction.Status.PENDING))
            throw new AlreadyExistsException("User already has a pending invitiation / application.");

        ProjectInteraction newInvitation = ProjectInteraction.builder()
                .user(userToBeInvited)
                .project(existingProject)
                .type(ProjectInteraction.Type.INVITATION)
                .status(ProjectInteraction.Status.PENDING)
                .build();

        interactionRepo.save(newInvitation);

        String message = "You have been invited to join project " + existingProject.getTitle() + "#" + projectId;
        notificationRepo
                .save(Notification.builder()
                        .title("New project invitation")
                        .message(message)
                        .user(userToBeInvited)
                        .build());

        return new BasicMessageDto("Project invitation has been succesfully sent.");
    }

    @Override
    public List<ProjectInteractionDto> getUserInvitations() {
        Long loggedInUserId = userService.getLoggedInUserId();
        return interactionRepo
                .findAllByUserIdAndTypeAndStatus(loggedInUserId, ProjectInteraction.Type.INVITATION,
                        ProjectInteraction.Status.PENDING)
                .stream()
                .map(this::interactionToProjectInteractionDtoMapper).toList();
    }

    @Override
    public List<ProjectInteractionDto> getUserApplications() {
        Long loggedInUserId = userService.getLoggedInUserId();
        return interactionRepo
                .findAllByUserIdAndTypeAndStatus(loggedInUserId, ProjectInteraction.Type.APPLICATION,
                        ProjectInteraction.Status.PENDING)
                .stream()
                .map(this::interactionToProjectInteractionDtoMapper).toList();
    }

    @Override
    public List<UserInteractionDto> getProjectInvitations(Long projectId) {
        validateThatLoggedInUserIsOwner(projectId);
        return interactionRepo
                .findAllByProjectIdAndTypeAndStatus(projectId, ProjectInteraction.Type.INVITATION,
                        ProjectInteraction.Status.PENDING)
                .stream()
                .map(this::interactionToUserInteractionDtoMapper).toList();
    }

    @Override
    public List<UserInteractionDto> getProjectApplications(Long projectId) {
        validateThatLoggedInUserIsOwner(projectId);
        return interactionRepo
                .findAllByProjectIdAndTypeAndStatus(projectId, ProjectInteraction.Type.APPLICATION,
                        ProjectInteraction.Status.PENDING)
                .stream()
                .map(this::interactionToUserInteractionDtoMapper).toList();
    }

    @Override
    @Transactional
    public BasicMessageDto acceptApplication(Long applicationId) {
        ProjectInteraction application = findProjectInteraction(applicationId);
        validateActiveApplication(application);
        Project project = returnProjectForOwner(application.getProject().getId());

        User newMember = application.getUser();

        if (projectMemberRepo.existsByUserIdAndProjectId(newMember.getId(), project.getId()))
            throw new UserAlreadyMemberException();

        application.setResponseDate(LocalDateTime.now());
        application.setStatus(ProjectInteraction.Status.ACCEPTED);

        interactionRepo.save(application);

        projectMemberRepo.save(ProjectMember.builder()
                .user(newMember)
                .project(project)
                .projectRole(ProjectMember.Role.USER)
                .build());

        String message = "Your application to join project " + project.getTitle() + "#" + project.getId()
                + "has been accepted";

        notificationRepo.save(Notification.builder()
                .title("Application accepted")
                .message(message)
                .user(newMember)
                .build());

        return new BasicMessageDto("User application to join the project has been accepted");
    }

    @Override
    @Transactional
    public BasicMessageDto declineApplication(Long applicationId) {
        ProjectInteraction application = findProjectInteraction(applicationId);

        validateActiveApplication(application);
        Project project = returnProjectForOwner(application.getProject().getId());

        application.setResponseDate(LocalDateTime.now());
        application.setStatus(ProjectInteraction.Status.DECLINED);

        interactionRepo.save(application);

        String message = "Your application to join project " + project.getTitle() + "#" + project.getId()
                + "has been declined";

        notificationRepo.save(Notification.builder()
                .title("Application declined")
                .message(message)
                .user(application.getUser())
                .build());

        return new BasicMessageDto("User application to join the project has been declined");
    }

    @Override
    @Transactional
    public BasicMessageDto acceptInvitation(Long invitationId) {
        Long loggedInUserId = userService.getLoggedInUserId();
        ProjectInteraction invitation = findProjectInteraction(invitationId);
        validateActiveInvitation(invitation);

        User user = invitation.getUser();
        if (user.getId() != loggedInUserId)
            throw new ForbiddenException("Users are not allowed to manage other user's invitations");

        Project project = invitation.getProject();
        if (projectMemberRepo.existsByUserIdAndProjectId(loggedInUserId, project.getId()))
            throw new UserAlreadyMemberException();

        invitation.setResponseDate(LocalDateTime.now());
        invitation.setStatus(ProjectInteraction.Status.ACCEPTED);

        interactionRepo.save(invitation);

        projectMemberRepo.save(ProjectMember.builder()
                .user(userRepository.findById(loggedInUserId).orElseThrow(() -> new AuthenticationRuntimeException()))
                .project(project)
                .projectRole(ProjectMember.Role.USER)
                .build());

        String message = "User with email of " + user.getEmail() + " has accepted your invitation to join project "
                + project.getTitle() + "#" + project.getId();

        notificationRepo.save(Notification.builder()
                .title("Invitation accepted")
                .message(message)
                .user(getProjectOwner(project.getId()).getUser())
                .build());

        return new BasicMessageDto("Project invitation has succefully been accepted.");
    }

    @Override
    public BasicMessageDto declineInvitation(Long invitationId) {
        Long loggedInUserId = userService.getLoggedInUserId();
        ProjectInteraction invitation = findProjectInteraction(invitationId);
        validateActiveInvitation(invitation);

        User user = invitation.getUser();

        if (user.getId() != loggedInUserId)
            throw new ForbiddenException("Users are not allowed to manage other user's invitations");

        invitation.setResponseDate(LocalDateTime.now());
        invitation.setStatus(ProjectInteraction.Status.DECLINED);
        interactionRepo.save(invitation);

        Project project = invitation.getProject();

        String message = "User with email of " + user.getEmail() + " has declined your invitation to join project "
                + project.getTitle() + "#" + project.getId();

        notificationRepo.save(Notification.builder()
                .title("Invitation declined")
                .message(message)
                .user(getProjectOwner(project.getId()).getUser())
                .build());
        return new BasicMessageDto("Project invitation has succefully been declined.");
    }

    @Override
    public BasicMessageDto cancelApplication(Long applicationId) {
        Long loggedInUserId = userService.getLoggedInUserId();
        ProjectInteraction application = findProjectInteraction(applicationId);
        validateActiveApplication(application);
        if (application.getUser().getId() != loggedInUserId)
            throw new ForbiddenException("Users are not allowed to manage other user's applications");
        interactionRepo.delete(application);
        return new BasicMessageDto("Project application has succefully been canceled.");

    }

    @Override
    public BasicMessageDto cancelInvitation(Long invitationId) {
        ProjectInteraction invitation = findProjectInteraction(invitationId);
        validateThatLoggedInUserIsOwner(invitation.getProject().getId());
        validateActiveInvitation(invitation);
        interactionRepo.delete(invitation);
        return new BasicMessageDto("Project invitation has been succefully canceled.");
    }

    @Override
    @Transactional
    public BasicMessageDto kickMemberOut(Long projectMemberId) {
        ProjectMember toBeKickedOut = projectMemberRepo.findById(projectMemberId)
                .orElseThrow(() -> new InvalidInputException("project member id", projectMemberId));

        if (toBeKickedOut.getProjectRole() == ProjectMember.Role.OWNER)
            throw new ForbiddenException("It is imposiible to kick the owner of the project out.");

        Project project = returnProjectForOwner(toBeKickedOut.getProject().getId());

        projectMemberRepo.deleteById(projectMemberId);

        String message = "The project owner has excluded you from the project " + project.getTitle() + "#"
                + project.getId();

        notificationRepo.save(Notification.builder()
                .title("You have been removed from the project.")
                .message(message)
                .user(toBeKickedOut.getUser())
                .build());

        return new BasicMessageDto("Project member has been successfully excluded from the project.");
    }

    @Override
    @Transactional
    public BasicMessageDto leaveProject(Long projectId) {
        Long loggedInUserId = userService.getLoggedInUserId();
        ProjectMember projectMember = projectMemberRepo.findByUserIdAndProjectId(loggedInUserId, projectId).orElseThrow(
                () -> new InvalidInputException("You are not a part of the project with the id of " + projectId));

        if (projectMember.getProjectRole() == ProjectMember.Role.OWNER)
            throw new ForbiddenException("Owner cannot leave the project. Consider deleting it.");

        projectMemberRepo.delete(projectMember);

        ProjectMember owner = getProjectOwner(projectId);
        User whoLeft = projectMember.getUser();
        Project project = projectMember.getProject();

        String message = "The user with the email of " + whoLeft.getEmail() + " has left your project "
                + project.getTitle() + "#"
                + project.getId();

        notificationRepo.save(Notification.builder()
                .title("User has left the project.")
                .message(message)
                .user(owner.getUser())
                .build());
        return new BasicMessageDto("You have succesfully left the project.");
    }

    private ProjectConfiguration getProjectConfig(Project project) {
        ProjectConfiguration config = project.getConfig();

        if (config == null)
            throw new EntityNotFoundException(
                    "Configuraion associated with project with the id of " + project.getId() + " is missing.");

        return config;
    }

    private void validateActiveInvitation(ProjectInteraction invitation) {
        if (invitation.getType() != ProjectInteraction.Type.INVITATION)
            throw new InvalidInteractionException("This interaction is not an invitation.");

        if (invitation.getStatus() != ProjectInteraction.Status.PENDING)
            throw new InvalidInteractionException("This invitation has already been reviewed.");
    }

    private void validateActiveApplication(ProjectInteraction application) {
        if (application.getType() != ProjectInteraction.Type.APPLICATION)
            throw new InvalidInteractionException("This interaction is not an application.");

        if (application.getStatus() != ProjectInteraction.Status.PENDING)
            throw new InvalidInteractionException("This application has already been reviewew.");
    }

    private ProjectInteraction findProjectInteraction(Long interactionId) {
        if (interactionId == null || interactionId < 1)
            throw new InvalidInputException("project interaction ID", interactionId);

        return interactionRepo.findById(interactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No project interaction found with the id of " + interactionId));
    }

    private Project returnProjectForOwner(Long projectId) {
        validateThatLoggedInUserIsOwner(projectId);
        return projectRepo.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project", projectId));
    }

    private void validateThatLoggedInUserIsOwner(Long projectId) {
        Long loggedInUserId = userService.getLoggedInUserId();
        if (!projectMemberRepo.existsByUserIdAndProjectIdAndProjectRole(loggedInUserId, projectId,
                ProjectMember.Role.OWNER))
            throw new ForbiddenException("You are not the owner and cannot perform this action.");
    }

    private ProjectMember getProjectOwner(Long projectId) {
        if (projectId == null || projectId <= 0)
            throw new InvalidInputException("project ID", projectId);

        List<ProjectMember> owners = projectMemberRepo.findByProjectIdAndProjectRole(projectId,
                ProjectMember.Role.OWNER);

        if (owners.isEmpty())
            throw new EntityNotFoundException("No owner found for the project with the id of " + projectId);

        if (owners.size() > 1) {
            log.warn("Multiple owners found for project {}", projectId);
        }

        return owners.get(0);
    }

    private Pageable validatePageableInput(Integer page, Integer size, Long resultsTotal, String sortBy,
            String sortDirection) {
        if (page != null && page < 1)
            throw new InvalidInputException("page", page);
        if (size < 1)
            throw new InvalidInputException("size", size);

        Sort sort = sortDirection.toLowerCase().equals("desc") ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return page == null ? PageRequest.of(0, Integer.MAX_VALUE, sort) : PageRequest.of(page - 1, size, sort);
    }

    private ProjectSortBy parseProjectSortBy(String sortBy) {
        try {
            return ProjectSortBy.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Default behaviour
            return ProjectSortBy.CREATEDAT;
        }
    }

    private PublicProjectDto projectToPublicProjectDto(Project project, ProjectMember owner) {
        if (project == null)
            throw new InvalidInputException("Project", null);

        Long loggedInUserId = userService.getLoggedInUserId();

        ProjectMember projectMember = projectMemberRepo.findByUserIdAndProjectId(loggedInUserId, project.getId())
                .orElse(null);

        boolean hasPendingRequest = projectMember != null ? false
                : interactionRepo.existsByUserIdAndProjectIdAndStatus(
                        loggedInUserId, project.getId(), ProjectInteraction.Status.PENDING);

        owner = owner != null ? owner : getProjectOwner(project.getId());

        return PublicProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .owner(ownerToShortDto(owner))
                .member(projectMember == null ? false : true)
                .memberSince(projectMember == null ? null : projectMember.getJoinDate())
                .hasPendingRequest(hasPendingRequest)
                .projectRole(projectMember != null ? projectMember.getProjectRole() : null)
                .build();
    }

    private OwnerShortDto ownerToShortDto(ProjectMember projectMember) {
        User user = projectMember.getUser();
        if (user == null)
            throw new EntityNotFoundException("User associated with the project member is missing.");

        return OwnerShortDto.builder()
                .userId(user.getId())
                .memberId(projectMember.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail()).build();
    }

    private UserInteractionDto interactionToUserInteractionDtoMapper(ProjectInteraction interaction) {
        if (interaction == null)
            throw new InvalidInputException("project interaction", null);

        User user = interaction.getUser();
        if (user == null)
            throw new EntityNotFoundException("User associated with the project interaction is missing.");

        UserShortDto userShortInfo = UserShortDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail()).build();
        return UserInteractionDto.builder()
                .id(interaction.getId())
                .user(userShortInfo)
                .initAt(interaction.getInitAt())
                .build();
    }

    private ProjectInteractionDto interactionToProjectInteractionDtoMapper(ProjectInteraction interaction) {
        if (interaction == null)
            throw new InvalidInputException("project interaction", null);

        Project project = interaction.getProject();
        if (project == null)
            throw new EntityNotFoundException("Project associated with the project interaction is missing.");

        ProjectShortDto projectShortInfo = ProjectShortDto.builder().id(project.getId()).title(project.getTitle())
                .build();
        OwnerShortDto ownerShortInfo = ownerToShortDto(getProjectOwner(project.getId()));

        return ProjectInteractionDto.builder()
                .id(interaction.getId())
                .project(projectShortInfo)
                .owner(ownerShortInfo)
                .initAt(interaction.getInitAt())
                .build();
    }

    private ProjectMemberInfoDto projectMemberToUserPublicInfoDto(ProjectMember member) {
        if (member == null)
            throw new InvalidInputException("member", null);

        User user = member.getUser();
        if (user == null)
            throw new EntityNotFoundException("User associated with the project member is missing.");

        return ProjectMemberInfoDto.builder()
                .userId(user.getId())
                .projectMemberId(member.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .projectRole(member.getProjectRole())
                .joinDate(member.getJoinDate())
                .build();
    }
}
