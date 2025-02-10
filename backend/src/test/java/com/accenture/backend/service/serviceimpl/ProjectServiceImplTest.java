package com.accenture.backend.service.serviceimpl;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.accenture.backend.entity.User;
import com.accenture.backend.dto.request.AcceptProjectDto;
import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.BasicNestedResponseDto;
import com.accenture.backend.dto.response.ProjectDto;
import com.accenture.backend.dto.response.ProjectInteractionDto;
import com.accenture.backend.dto.response.ProjectMemberInfoDto;
import com.accenture.backend.dto.response.PublicProjectDto;
import com.accenture.backend.dto.response.UserInteractionDto;
import com.accenture.backend.entity.Notification;
import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.ProjectConfiguration;
import com.accenture.backend.entity.ProjectInteraction;

import com.accenture.backend.enums.Role;

import com.accenture.backend.repository.NotificationRepository;
import com.accenture.backend.repository.ProjectConfigurationRepository;
import com.accenture.backend.repository.ProjectInteractionRepository;
import com.accenture.backend.repository.ProjectMemberRepository;
import com.accenture.backend.repository.ProjectRepository;
import com.accenture.backend.repository.UserRepository;

import com.accenture.backend.service.UserService;
import com.accenture.backend.exception.custom.EntityNotFoundException;
import com.accenture.backend.exception.custom.ForbiddenException;
import com.accenture.backend.exception.custom.InvalidInputException;
import com.accenture.backend.exception.custom.MaxProjectOwnerLimitExceededException;
import com.accenture.backend.exception.custom.PageOutOfRangeException;

public class ProjectServiceImplTest {
        @Mock
        private ProjectRepository projectRepo;

        @Mock
        private ProjectMemberRepository projectMemberRepo;

        @Mock
        private ProjectConfigurationRepository configRepo;

        @Mock
        private UserRepository userRepo;

        @Mock
        private ProjectInteractionRepository interactionRepo;

        @Mock
        private NotificationRepository notificationRepo;

        @Mock
        private UserService userService;

        @InjectMocks
        private ProjectServiceImpl projectService;

        private User ownerUserInPublic;
        private User ownerUserInPrivate;
        private User pendingUserInPublic;
        private User pendingUserInPrivate;

        private Project publicProject;
        private Project privateProject;

        private ProjectMember ownerMemberInPublic;
        private ProjectMember ownerMemberInPrivate;

        @BeforeEach
        void setUp() throws NoSuchFieldException, IllegalAccessException {
                MockitoAnnotations.openMocks(this);

                Field maxProjectAmountAllowedField = ProjectServiceImpl.class
                                .getDeclaredField("maxProjectAmountAllowed");
                maxProjectAmountAllowedField.setAccessible(true);
                maxProjectAmountAllowedField.set(projectService, 5);

                //////////////

                ownerUserInPublic = User.builder().id(1L).firstName("Deniss").lastName("Solovjovs")
                                .email("solo@deni.com")
                                .role(Role.USER).build();
                ownerUserInPrivate = User.builder().id(2L).firstName("Davids").lastName("Solovjovs")
                                .email("solo@davi.com")
                                .role(Role.USER).build();
                pendingUserInPublic = User.builder().id(3L).firstName("Man").lastName("One")
                                .email("solo@deni2.com")
                                .role(Role.USER).build();
                pendingUserInPrivate = User.builder().id(4L).firstName("Man").lastName("Two")
                                .email("solo@davi2.com")
                                .role(Role.USER).build();

                publicProject = Project.builder().id(1L)
                                .config(ProjectConfiguration.builder().id(1L).isPublic(true).build())
                                .title("Public project").description("This is a public project").build();
                privateProject = Project.builder().id(2L)
                                .config(ProjectConfiguration.builder().id(2L).isPublic(false).build())
                                .title("Private project").description("This is a private  project").build();

                ownerMemberInPublic = ProjectMember.builder().id(1L).project(publicProject).user(ownerUserInPublic)
                                .projectRole(ProjectMember.Role.OWNER).build();
                ownerMemberInPrivate = ProjectMember.builder().id(1L).project(privateProject).user(ownerUserInPrivate)
                                .projectRole(ProjectMember.Role.OWNER).build();

                when(projectRepo.findById(publicProject.getId())).thenReturn(Optional.of(publicProject));
                when(projectRepo.findById(privateProject.getId())).thenReturn(Optional.of(privateProject));

                when(userRepo.findById(ownerUserInPublic.getId())).thenReturn(Optional.of(ownerUserInPublic));
                when(userRepo.findById(ownerUserInPrivate.getId())).thenReturn(Optional.of(ownerUserInPrivate));

                when(projectMemberRepo.findById(ownerMemberInPublic.getId()))
                                .thenReturn(Optional.of(ownerMemberInPublic));
                when(projectMemberRepo.findById(ownerMemberInPrivate.getId()))
                                .thenReturn(Optional.of(ownerMemberInPrivate));

                when(projectMemberRepo.existsByUserIdAndProjectId(ownerUserInPublic.getId(), publicProject.getId()))
                                .thenReturn(true);
                when(projectMemberRepo.existsByUserIdAndProjectId(ownerUserInPrivate.getId(), privateProject.getId()))
                                .thenReturn(true);
                when(projectMemberRepo.existsByUserIdAndProjectId(ownerUserInPublic.getId(), privateProject.getId()))
                                .thenReturn(false);
                when(projectMemberRepo.existsByUserIdAndProjectId(ownerUserInPrivate.getId(), publicProject.getId()))
                                .thenReturn(false);

                when(projectMemberRepo.findByUserIdAndProjectId(ownerUserInPublic.getId(), publicProject.getId()))
                                .thenReturn(Optional.of(ownerMemberInPublic));
                when(projectMemberRepo.findByUserIdAndProjectId(ownerUserInPrivate.getId(), privateProject.getId()))
                                .thenReturn(Optional.of(ownerMemberInPrivate));
                when(projectMemberRepo.findByUserIdAndProjectId(ownerUserInPublic.getId(), privateProject.getId()))
                                .thenReturn(Optional.empty());
                when(projectMemberRepo.findByUserIdAndProjectId(ownerUserInPrivate.getId(), publicProject.getId()))
                                .thenReturn(Optional.empty());

                when(projectMemberRepo.findByProjectIdAndProjectRole(publicProject.getId(), ProjectMember.Role.OWNER))
                                .thenReturn(Collections.singletonList(ownerMemberInPublic));
                when(projectMemberRepo.findByProjectIdAndProjectRole(privateProject.getId(), ProjectMember.Role.OWNER))
                                .thenReturn(Collections.singletonList(ownerMemberInPrivate));

                when(projectMemberRepo.existsByUserIdAndProjectIdAndProjectRole(ownerUserInPublic.getId(),
                                publicProject.getId(),
                                ProjectMember.Role.OWNER)).thenReturn(true);

                when(interactionRepo.existsByUserIdAndProjectIdAndStatus(pendingUserInPublic.getId(),
                                publicProject.getId(),
                                ProjectInteraction.Status.PENDING)).thenReturn(true);
                when(interactionRepo.existsByUserIdAndProjectIdAndStatus(pendingUserInPrivate.getId(),
                                privateProject.getId(),
                                ProjectInteraction.Status.PENDING)).thenReturn(true);
        }

        @Test
        void getProjectInfo_NonMemberAndPrivateProject_ThrowsException() {
                // Non-member tries to access a private project
                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());

                assertThrows(ForbiddenException.class, () -> projectService.getProjectInfo(privateProject.getId()));
                verify(projectRepo, times(1)).findById(privateProject.getId());
                verify(projectMemberRepo, times(0)).existsByUserIdAndProjectId(ownerUserInPrivate.getId(),
                                privateProject.getId());
        }

        @Test
        void getProjectInfo_MemberAndPrivateProject_ReturnsDto() {
                // Member tries to access a private project
                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPrivate.getId());
                PublicProjectDto resultOkPrivate = projectService.getProjectInfo(privateProject.getId());

                assertNotNull(resultOkPrivate, "The result should not be null");
                verify(projectRepo, times(1)).findById(privateProject.getId());
                verify(projectMemberRepo, times(1)).existsByUserIdAndProjectId(ownerUserInPrivate.getId(),
                                privateProject.getId());
        }

        @Test
        void getProjectInfo_MemberAndPublicProject_ReturnsDto() {
                // Member tries to access a public project
                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());
                PublicProjectDto resultOkPublic = projectService.getProjectInfo(publicProject.getId());

                assertNotNull(resultOkPublic, "The result should not be null");
                verify(projectRepo, times(1)).findById(publicProject.getId());
                verify(projectMemberRepo, times(0)).existsByUserIdAndProjectId(ownerUserInPublic.getId(),
                                publicProject.getId());
        }

        @Test
        void getPublicProjects_NoProjects_ReturnsEmptyPage() {
                when(projectRepo.countAllByConfigIsPublicTrue()).thenReturn(0L);
                Page<PublicProjectDto> result = projectService.getPublicProjects(0, 10, "title", "asc");

                assertNotNull(result, "The result should not be null");
                assertTrue(result.isEmpty(), "The result should be empty");
                verify(projectRepo, times(1)).countAllByConfigIsPublicTrue();
                verify(projectRepo, times(0)).findAllByConfigIsPublicTrue(any(Pageable.class));
        }

        @Test
        void getPublicProjects_InvalidInputForPageable_ThrowsException() {
                Page<Project> projectPage = new PageImpl<>(Collections.singletonList(publicProject));
                when(projectRepo.findAllByConfigIsPublicTrue(any(Pageable.class))).thenReturn(projectPage);
                when(projectRepo.countAllByConfigIsPublicTrue()).thenReturn(1L);

                assertThrows(InvalidInputException.class,
                                () -> projectService.getPublicProjects(-1, 10, "title", "asc"));

                assertThrows(InvalidInputException.class,
                                () -> projectService.getPublicProjects(1, 0, "title", "asc"));

                assertThrows(PageOutOfRangeException.class,
                                () -> projectService.getPublicProjects(2, 5, "title", "asc"));
        }

        @Test
        void getPublicProjects_Valid_ReturnsDto() {
                Page<Project> projectPage = new PageImpl<>(Collections.singletonList(publicProject));
                when(projectRepo.findAllByConfigIsPublicTrue(any(Pageable.class))).thenReturn(projectPage);
                when(projectRepo.countAllByConfigIsPublicTrue()).thenReturn(1L);
                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());

                Page<PublicProjectDto> result = projectService.getPublicProjects(1, 5, "title", "asc");

                assertFalse(result.isEmpty(), "The result should not be empty");
                assertEquals(1L, result.getTotalElements(), "The total number of elements should be 1");
                assertEquals(1, result.getContent().size(), "The content size should be 1");

                PublicProjectDto publicProjectDto = result.getContent().get(0);
                assertEquals(publicProject.getId(), publicProjectDto.getId(), "Project IDs should match");
                assertEquals(publicProject.getTitle(), publicProjectDto.getTitle(), "Project titles should match");
                assertEquals(publicProject.getDescription(), publicProjectDto.getDescription(),
                                "Project descriptions should match");
                assertEquals(publicProject.getCreatedAt(), publicProjectDto.getCreatedAt(),
                                "Project creation times should match");
                assertNotNull(publicProjectDto.getOwner(), "Owner should not be null");
                assertEquals(ownerMemberInPublic.getId(), publicProjectDto.getOwner().getUserId(),
                                "Owner user ID should match");
                assertTrue(publicProjectDto.isMember(), "Project member flag should be true");
                assertFalse(publicProjectDto.isHasPendingRequest(), "Pending request flag should be false");
                assertEquals(ProjectMember.Role.OWNER, publicProjectDto.getProjectRole(),
                                "Project role should be OWNER");

                verify(projectRepo, times(1)).findAllByConfigIsPublicTrue(any(Pageable.class));

                // Case when non-member is logged in
                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPrivate.getId());
                Page<PublicProjectDto> result2 = projectService.getPublicProjects(1, 5, "title", "asc");
                PublicProjectDto publicProjectDto2 = result2.getContent().get(0);
                assertFalse(publicProjectDto2.isMember(), "Member flag should be false");
                assertFalse(publicProjectDto2.isHasPendingRequest(), "Pending request flag should be false");

                // Case when has a pending ProjectInteraction
                when(userService.getLoggedInUserId()).thenReturn(pendingUserInPublic.getId());
                Page<PublicProjectDto> result3 = projectService.getPublicProjects(1, 5, "title", "asc");
                PublicProjectDto publicProjectDto3 = result3.getContent().get(0);
                assertFalse(publicProjectDto3.isMember(), "Member flag should be false");
                assertTrue(publicProjectDto3.isHasPendingRequest(), "Pending request flag should be true");
        }

        @Test
        void getProjectMembers_NonMember_ThrowsException() {
                Long loggedInUserId = ownerMemberInPublic.getId();
                Long privateProjectId = privateProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                assertThrows(ForbiddenException.class,
                                () -> projectService.getProjectMembers(privateProjectId, 1, 10, "asc"));
                verify(projectMemberRepo, times(1)).existsByUserIdAndProjectId(ownerMemberInPublic.getId(),
                                privateProjectId);
        }

        @Test
        void getProjectMembers_NoResults_ReturnsEmptyPage() {
                Long loggedInUserId = ownerMemberInPublic.getId();
                Long projectId = publicProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(projectMemberRepo.countAllByProjectId(projectId)).thenReturn(0L);

                Page<ProjectMemberInfoDto> result = projectService.getProjectMembers(projectId, 0, 10, "asc");

                assertNotNull(result, "Result should not be null");
                assertTrue(result.isEmpty(), "Result should be empty");
                verify(projectMemberRepo, times(1)).countAllByProjectId(projectId);
                verify(projectMemberRepo, times(0)).findByProjectId(eq(projectId), any(Pageable.class));
        }

        @Test
        void getProjectMembers_ValidRequest_ReturnsProjectMembers() {
                Long loggedInUserId = ownerUserInPublic.getId();
                Long projectId = publicProject.getId();

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(projectMemberRepo.countAllByProjectId(projectId)).thenReturn(1L);
                when(projectMemberRepo.findByProjectId(eq(projectId), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Collections.singletonList(ownerMemberInPublic)));

                Page<ProjectMemberInfoDto> result = projectService.getProjectMembers(projectId, 1, 10, "asc");

                assertFalse(result.isEmpty(), "Result should not be empty");
                assertEquals(1L, result.getTotalElements(), "Total elements should be 1");
                assertEquals(1, result.getContent().size(), "Content size should be 1");
                ProjectMemberInfoDto dto = result.getContent().get(0);

                assertEquals(ownerMemberInPublic.getUser().getId(), dto.getUserId(), "User ID should match");
                assertEquals(ownerMemberInPublic.getUser().getEmail(), dto.getEmail(), "Email should match");
                assertEquals(ownerMemberInPublic.getUser().getFirstName(), dto.getFirstName(),
                                "First name should match");
                assertEquals(ownerMemberInPublic.getUser().getLastName(), dto.getLastName(), "Last name should match");
                assertEquals(ownerMemberInPublic.getProjectRole(), dto.getProjectRole(), "Project role should match");
                assertEquals(ownerMemberInPublic.getJoinDate(), dto.getJoinDate(), "Join date should match");

                verify(projectMemberRepo, times(1)).findByProjectId(eq(projectId), any(Pageable.class));
        }

        @Test
        void createNewProject_MaxLimitExceeded_ThrowsException() {
                Long loggedInUserId = ownerUserInPublic.getId();
                AcceptProjectDto dto = new AcceptProjectDto("New Project", "Description of the project", true, 10);

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER))
                                .thenReturn(10L);

                assertThrows(MaxProjectOwnerLimitExceededException.class, () -> projectService.createNewProject(dto));

                verify(projectRepo, times(0)).save(any(Project.class));
                verify(configRepo, times(0)).save(any(ProjectConfiguration.class));
                verify(projectMemberRepo, times(0)).save(any(ProjectMember.class));
        }

        @Test
        void createNewProject_Success_ReturnsDto() {
                Long loggedInUserId = ownerUserInPublic.getId();
                String title = "New Project";
                String description = "This is a new description";
                Boolean isPublic = true;
                Integer maxParticipants = 10;

                AcceptProjectDto dto = AcceptProjectDto.builder()
                                .title(title)
                                .description(description)
                                .isPublic(isPublic)
                                .maxParticipants(maxParticipants).build();

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER))
                                .thenReturn(0L);

                Project newProject = Project.builder().id(1L).title(title).description(description).build();
                ProjectConfiguration config = ProjectConfiguration.builder().id(1L).isPublic(isPublic)
                                .maxParticipants(maxParticipants).build();
                ProjectMember owner = ProjectMember.builder().user(ownerUserInPublic).project(newProject)
                                .projectRole(ProjectMember.Role.OWNER).build();

                when(projectRepo.save(any(Project.class))).thenReturn(newProject);
                when(configRepo.save(any(ProjectConfiguration.class))).thenReturn(config);
                when(projectMemberRepo.save(any(ProjectMember.class))).thenReturn(owner);

                BasicNestedResponseDto<ProjectDto> result = projectService.createNewProject(dto);

                assertNotNull(result, "Result should not be null");
                assertEquals("New project has been successfully created", result.getMessage(), "Message should match");
                assertNotNull(result.getData(), "Data should not be null");
                assertEquals(1L, result.getData().getProjectInfo().getId(), "Project id should match");
                assertEquals(title, result.getData().getProjectInfo().getTitle(), "Project title should match");
                assertEquals(description, result.getData().getProjectInfo().getDescription(),
                                "Project description should match");
                assertEquals(isPublic, result.getData().getConfig().getIsPublic(), "isPublic should be true");
                assertEquals(loggedInUserId, result.getData().getProjectInfo().getOwner().getUserId(),
                                "UserId should match");
                assertEquals(maxParticipants, result.getData().getConfig().getMaxParticipants(),
                                "maxParticipants should match.");

                verify(projectRepo, times(1)).save(any(Project.class));
                verify(configRepo, times(1)).save(any(ProjectConfiguration.class));
                verify(projectMemberRepo, times(1)).save(any(ProjectMember.class));
        }

        @Test
        void updateExistingProject_NotOwnerTryingToUpdate_ThrowsForbiddenException() {
                Long projectId = 100L; // Not the owner ID
                AcceptProjectDto dto = new AcceptProjectDto("Updated Title", "Updated Description", true, 10);

                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.updateExistingProject(projectId, dto));
                assertEquals("You are not the owner and cannot perform this action.", e.getMessage());
                verify(projectRepo, times(0)).save(any());
        }

        @Test
        void updateExistingProject_MissingConfig_ThrowsEntityNotFoundException() {
                Long projectId = publicProject.getId();
                AcceptProjectDto dto = new AcceptProjectDto("Updated Title", "Updated Description", true, 10);

                Project existingProject = Project.builder()
                                .id(projectId)
                                .title("Updated Title")
                                .description("Updated Description")
                                .config(null)
                                .build();
                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());
                when(projectRepo.findById(projectId)).thenReturn(Optional.of(existingProject));
                assertThrows(EntityNotFoundException.class, () -> projectService.updateExistingProject(projectId, dto));
                verify(projectRepo, times(0)).save(any());
        }

        @Test
        void updateExistingProject_Success_ReturnsUpdatedProjectDto() {
                Long projectId = 1L;

                String oldTitle = "Old Title";
                String oldDescription = "Old Description";
                Boolean oldIsPublic = true;
                Integer oldMaxParticipants = 6;

                AcceptProjectDto dto = new AcceptProjectDto("Updated Title", "Updated Description", false, 10);

                Project existingProject = Project.builder()
                                .id(projectId)
                                .title(oldTitle)
                                .description(oldDescription)
                                .build();

                existingProject.setConfig(
                                ProjectConfiguration.builder().id(1L).isPublic(oldIsPublic)
                                                .maxParticipants(oldMaxParticipants).build());

                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());
                when(projectRepo.findById(projectId)).thenReturn(Optional.of(existingProject));
                when(projectRepo.save(existingProject)).thenReturn(existingProject);

                BasicNestedResponseDto<ProjectDto> result = projectService.updateExistingProject(projectId, dto);

                assertNotNull(result);
                assertEquals("Existing project has been succefully updated", result.getMessage());
                assertEquals("Updated Title", result.getData().getProjectInfo().getTitle());
                assertEquals("Updated Description", result.getData().getProjectInfo().getDescription());
                assertFalse(result.getData().getConfig().getIsPublic());
                assertEquals(10, result.getData().getConfig().getMaxParticipants());

                verify(projectRepo, times(1)).save(existingProject);
        }

        @Test
        void deleteProject_Valid() {
                Long projectId = 1L;

                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());
                when(projectMemberRepo.findUsersByProjectId(projectId))
                                .thenReturn(Collections.singletonList(ownerUserInPublic));
                doNothing().when(projectRepo).deleteById(projectId);
                when(notificationRepo.save(any(Notification.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                BasicMessageDto result = projectService.deleteProject(projectId);

                assertNotNull(result);
                assertEquals("Project has been succesfully deleted", result.getMessage());

                verify(projectRepo, times(1)).deleteById(projectId);
                verify(notificationRepo, times(1)).save(any(Notification.class));
        }

        @Test
        void getUserInvitations_NoAssociatedProject_ThrowsException() {
                Long userId = ownerUserInPublic.getId();
                ProjectInteraction interaction = createInteraction(ownerUserInPublic, null);

                when(userService.getLoggedInUserId()).thenReturn(userId);
                when(interactionRepo.findAllByUserIdAndTypeAndStatus(userId, ProjectInteraction.Type.INVITATION,
                                ProjectInteraction.Status.PENDING))
                                .thenReturn(List.of(interaction));

                EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
                                () -> projectService.getUserInvitations());
                assertEquals("Project associated with the project interaction is missing.", e.getMessage());
                verify(interactionRepo, times(1)).findAllByUserIdAndTypeAndStatus(userId,
                                ProjectInteraction.Type.INVITATION, ProjectInteraction.Status.PENDING);
        }

        @Test
        void getUserInvitations_Valid_ReturnsDto() {
                Long userId = ownerUserInPublic.getId();
                ProjectInteraction interaction = createInteraction(ownerUserInPublic, publicProject);

                when(userService.getLoggedInUserId()).thenReturn(userId);
                when(interactionRepo.findAllByUserIdAndTypeAndStatus(userId, ProjectInteraction.Type.INVITATION,
                                ProjectInteraction.Status.PENDING))
                                .thenReturn(List.of(interaction));

                List<ProjectInteractionDto> result = projectService.getUserInvitations();

                assertNotNull(result, "Result list should not be null");
                assertEquals(1, result.size());

                ProjectInteractionDto dto = result.get(0);
                assertEquals(1L, dto.getId());
                assertEquals(interaction.getInitAt(), dto.getInitAt());
                assertEquals(interaction.getProject().getId(), dto.getProject().getId());
                assertEquals(interaction.getProject().getTitle(), dto.getProject().getTitle());

                verify(interactionRepo, times(1)).findAllByUserIdAndTypeAndStatus(userId,
                                ProjectInteraction.Type.INVITATION, ProjectInteraction.Status.PENDING);
        }

        @Test
        void getProjectInvitations_NotOwner_ThrowsException() {
                Long loggedInUserId = 1000L;
                Long projectId = publicProject.getId();

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(projectMemberRepo.existsByUserIdAndProjectIdAndProjectRole(loggedInUserId, projectId,
                                ProjectMember.Role.OWNER)).thenReturn(false);

                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.getProjectInvitations(projectId));

                assertEquals("You are not the owner and cannot perform this action.", e.getMessage());
                verify(interactionRepo, times(0)).findAllByProjectIdAndTypeAndStatus(projectId,
                                ProjectInteraction.Type.INVITATION, ProjectInteraction.Status.PENDING);
        }

        @Test
        void getProjectInvitations_InteractionAssociatedUserIsNull_ThrowsException() {
                Long loggedInUserId = ownerUserInPublic.getId();
                Long projectId = publicProject.getId();

                ProjectInteraction interaction = createInteraction(null, publicProject);

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(projectMemberRepo.existsByUserIdAndProjectIdAndProjectRole(loggedInUserId, projectId,
                                ProjectMember.Role.OWNER)).thenReturn(true);
                when(interactionRepo.findAllByProjectIdAndTypeAndStatus(projectId, ProjectInteraction.Type.INVITATION,
                                ProjectInteraction.Status.PENDING)).thenReturn(Collections.singletonList(interaction));

                EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
                                () -> projectService.getProjectInvitations(projectId));

                assertEquals("User associated with the project interaction is missing.", e.getMessage());
                verify(userService, times(1)).getLoggedInUserId();
                verify(projectMemberRepo, times(1)).existsByUserIdAndProjectIdAndProjectRole(loggedInUserId, projectId,
                                ProjectMember.Role.OWNER);
                verify(interactionRepo, times(1)).findAllByProjectIdAndTypeAndStatus(projectId,
                                ProjectInteraction.Type.INVITATION,
                                ProjectInteraction.Status.PENDING);

        }

        @Test
        void getProjectInvitations_Valid_ReturnsDto() {
                Long loggedInUserId = ownerUserInPublic.getId();
                Long projectId = publicProject.getId();

                ProjectInteraction interaction = createInteraction(ownerUserInPrivate, privateProject);

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(projectMemberRepo.existsByUserIdAndProjectIdAndProjectRole(loggedInUserId, projectId,
                                ProjectMember.Role.OWNER)).thenReturn(true);
                when(interactionRepo.findAllByProjectIdAndTypeAndStatus(projectId, ProjectInteraction.Type.INVITATION,
                                ProjectInteraction.Status.PENDING)).thenReturn(Collections.singletonList(interaction));

                List<UserInteractionDto> result = projectService.getProjectInvitations(projectId);

                assertNotNull(result, "Result list should not be null");
                assertEquals(1, result.size());

                UserInteractionDto dto = result.get(0);
                assertEquals(1L, dto.getId());
                assertEquals(interaction.getInitAt(), dto.getInitAt());
                assertEquals(interaction.getUser().getId(), dto.getUser().getId());
                assertEquals(interaction.getUser().getEmail(), dto.getUser().getEmail());
                assertEquals(interaction.getUser().getFirstName(), dto.getUser().getFirstName());
                assertEquals(interaction.getUser().getLastName(), dto.getUser().getLastName());

                verify(userService, times(1)).getLoggedInUserId();
                verify(projectMemberRepo, times(1)).existsByUserIdAndProjectIdAndProjectRole(loggedInUserId, projectId,
                                ProjectMember.Role.OWNER);
                verify(interactionRepo, times(1)).findAllByProjectIdAndTypeAndStatus(projectId,
                                ProjectInteraction.Type.INVITATION,
                                ProjectInteraction.Status.PENDING);
        }

        private ProjectInteraction createInteraction(User user, Project project) {
                return ProjectInteraction.builder()
                                .id(1L)
                                .user(user)
                                .project(project)
                                .type(ProjectInteraction.Type.INVITATION)
                                .status(ProjectInteraction.Status.PENDING)
                                .build();
        }

}