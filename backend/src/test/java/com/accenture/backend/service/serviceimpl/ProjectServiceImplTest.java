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
import com.accenture.backend.dto.request.InvitationDto;
import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.BasicNestedResponseDto;
import com.accenture.backend.dto.response.ProjectConfigDto;
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
import com.accenture.backend.service.PremiumAccountService;
import com.accenture.backend.service.UserService;
import com.accenture.backend.exception.AlreadyExistsException;
import com.accenture.backend.exception.EntityNotFoundException;
import com.accenture.backend.exception.ForbiddenException;
import com.accenture.backend.exception.InvalidInputException;
import com.accenture.backend.exception.InvalidInteractionException;
import com.accenture.backend.exception.MaxParticipantsReachedException;
import com.accenture.backend.exception.MaxProjectOwnerLimitExceededException;
import com.accenture.backend.exception.UserAlreadyMemberException;

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

        @Mock
        private PremiumAccountService premiumAccountService;

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

        private ProjectInteraction pendingInPublic;
        private ProjectInteraction pendingInPrivate;

        @BeforeEach
        void setUp() throws NoSuchFieldException, IllegalAccessException {
                MockitoAnnotations.openMocks(this);

                Field maxProjectAmountAllowedField = ProjectServiceImpl.class
                                .getDeclaredField("maxProjectAmountAllowed");
                maxProjectAmountAllowedField.setAccessible(true);
                maxProjectAmountAllowedField.set(projectService, 5);

                Field maxProjectAmountAllowedWithPremiumField = ProjectServiceImpl.class
                                .getDeclaredField("maxProjectAmountAllowedWithPremium");
                maxProjectAmountAllowedWithPremiumField.setAccessible(true);
                maxProjectAmountAllowedWithPremiumField.set(projectService, 10);

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
                                .config(ProjectConfiguration.builder().id(1L).maxParticipants(2).isPublic(true).build())
                                .title("Public project").description("This is a public project").build();
                privateProject = Project.builder().id(2L)
                                .config(ProjectConfiguration.builder().id(2L).maxParticipants(2).isPublic(false)
                                                .build())
                                .title("Private project").description("This is a private  project").build();

                ownerMemberInPublic = ProjectMember.builder().id(1L).project(publicProject).user(ownerUserInPublic)
                                .projectRole(ProjectMember.Role.OWNER).build();
                ownerMemberInPrivate = ProjectMember.builder().id(1L).project(privateProject).user(ownerUserInPrivate)
                                .projectRole(ProjectMember.Role.OWNER).build();

                pendingInPublic = ProjectInteraction.builder()
                                .id(1L).user(pendingUserInPublic).project(publicProject)
                                .status(ProjectInteraction.Status.PENDING).type(ProjectInteraction.Type.APPLICATION)
                                .build();

                pendingInPrivate = ProjectInteraction.builder()
                                .id(2L).user(pendingUserInPrivate).project(privateProject)
                                .status(ProjectInteraction.Status.PENDING).type(ProjectInteraction.Type.INVITATION)
                                .build();

                when(projectRepo.findById(publicProject.getId())).thenReturn(Optional.of(publicProject));
                when(projectRepo.findById(privateProject.getId())).thenReturn(Optional.of(privateProject));

                when(userRepo.findById(ownerUserInPublic.getId())).thenReturn(Optional.of(ownerUserInPublic));
                when(userRepo.findById(ownerUserInPrivate.getId())).thenReturn(Optional.of(ownerUserInPrivate));
                when(userRepo.findById(pendingUserInPrivate.getId())).thenReturn(Optional.of(pendingUserInPrivate));
                when(userRepo.findById(pendingUserInPublic.getId())).thenReturn(Optional.of(pendingUserInPublic));

                configureProjectMemberQueries();

                when(interactionRepo.existsByUserIdAndProjectIdAndStatus(pendingUserInPublic.getId(),
                                publicProject.getId(),
                                ProjectInteraction.Status.PENDING)).thenReturn(true);
                when(interactionRepo.existsByUserIdAndProjectIdAndStatus(pendingUserInPrivate.getId(),
                                privateProject.getId(),
                                ProjectInteraction.Status.PENDING)).thenReturn(true);

                when(interactionRepo.findById(pendingInPublic.getId())).thenReturn(Optional.of(pendingInPublic));
                when(interactionRepo.findById(pendingInPrivate.getId())).thenReturn(Optional.of(pendingInPrivate));
        }

        private void configureProjectMemberQueries() {

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
                when(projectMemberRepo.existsByUserIdAndProjectIdAndProjectRole(ownerUserInPublic.getId(),
                                privateProject.getId(),
                                ProjectMember.Role.OWNER)).thenReturn(false);
                when(projectMemberRepo.existsByUserIdAndProjectIdAndProjectRole(ownerUserInPrivate.getId(),
                                publicProject.getId(),
                                ProjectMember.Role.OWNER)).thenReturn(false);
                when(projectMemberRepo.existsByUserIdAndProjectIdAndProjectRole(ownerUserInPrivate.getId(),
                                privateProject.getId(),
                                ProjectMember.Role.OWNER)).thenReturn(true);
        }

        @Test
        void getProjectInfo_NonExistentProject_ThrowsException() {
                Long loggedInUserId = ownerUserInPublic.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                InvalidInputException e = assertThrows(InvalidInputException.class,
                                () -> projectService.getProjectInfo(1234L));
                verify(projectRepo, times(1)).findById(1234L);
                verify(projectMemberRepo, times(0)).existsByUserIdAndProjectId(ownerUserInPrivate.getId(),
                                privateProject.getId());
                verify(projectMemberRepo, times(0)).findByUserIdAndProjectId(ownerUserInPrivate.getId(),
                                privateProject.getId());
        }

        @Test
        void getProjectInfo_NonMemberAndPrivateProject_ThrowsException() {
                // Non-member tries to access a private project
                Long loggedInUserId = ownerUserInPublic.getId();
                Long privateProjectId = privateProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.getProjectInfo(privateProjectId));
                assertEquals("You cannot see the private project's information unless you are a member.",
                                e.getMessage());
                verify(projectRepo, times(1)).findById(privateProjectId);
                verify(projectMemberRepo, times(1)).existsByUserIdAndProjectId(loggedInUserId, privateProjectId);
                verify(projectMemberRepo, times(0)).findByUserIdAndProjectId(loggedInUserId, privateProjectId);
        }

        @Test
        void getProjectInfo_MemberAndPrivateProject_ReturnsDto() {
                // Member tries to access a private project
                Long loggedInUserId = ownerUserInPrivate.getId();
                Long privateProjectId = privateProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                PublicProjectDto resultOkPrivate = projectService.getProjectInfo(privateProjectId);

                assertNotNull(resultOkPrivate, "The result should not be null");
                assertEquals(privateProject.getId(), resultOkPrivate.getId());
                assertEquals(privateProject.getTitle(), resultOkPrivate.getTitle());
                assertEquals(privateProject.getDescription(), resultOkPrivate.getDescription());
                assertEquals(loggedInUserId, resultOkPrivate.getOwner().getUserId());
                assertEquals(false, resultOkPrivate.isHasPendingRequest());
                assertEquals(ProjectMember.Role.OWNER, resultOkPrivate.getProjectRole());
                assertEquals(ownerMemberInPrivate.getJoinDate(), resultOkPrivate.getMemberSince());
                verify(projectRepo, times(1)).findById(privateProject.getId());
                verify(projectMemberRepo, times(1)).existsByUserIdAndProjectId(loggedInUserId,
                                privateProject.getId());
                verify(projectMemberRepo, times(1)).findByUserIdAndProjectId(loggedInUserId, privateProjectId);
        }

        @Test
        void getProjectInfo_NonMemberAndPublicProject_ReturnsDto() {
                Long loggedInUserId = ownerUserInPrivate.getId();
                Long publicProjectId = publicProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                PublicProjectDto resultDto = projectService.getProjectInfo(publicProjectId);

                assertNotNull(resultDto, "The result should not be null");
                assertEquals(publicProject.getId(), resultDto.getId());
                assertEquals(publicProject.getTitle(), resultDto.getTitle());
                assertEquals(publicProject.getDescription(), resultDto.getDescription());
                assertEquals(ownerMemberInPublic.getId(), resultDto.getOwner().getUserId());

                assertEquals(false, resultDto.isHasPendingRequest());
                assertEquals(null, resultDto.getProjectRole());
                assertEquals(null, resultDto.getMemberSince());
                verify(projectRepo, times(1)).findById(publicProject.getId());
                verify(projectMemberRepo, times(0)).existsByUserIdAndProjectId(loggedInUserId,
                                publicProject.getId());
                verify(projectMemberRepo, times(1)).findByUserIdAndProjectId(loggedInUserId, publicProjectId);
        }

        @Test
        void getProjectInfo_MemberAndPublicProject_ReturnsDto() {
                Long loggedInUserId = ownerMemberInPublic.getId();
                Long publicProjectId = publicProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                PublicProjectDto resultDto = projectService.getProjectInfo(publicProjectId);

                assertEquals(false, resultDto.isHasPendingRequest());
                assertEquals(ProjectMember.Role.OWNER, resultDto.getProjectRole());
                assertEquals(ownerMemberInPublic.getJoinDate(), resultDto.getMemberSince());
                verify(projectRepo, times(1)).findById(publicProject.getId());
                verify(projectMemberRepo, times(0)).existsByUserIdAndProjectId(loggedInUserId,
                                publicProject.getId());
                verify(projectMemberRepo, times(1)).findByUserIdAndProjectId(loggedInUserId, publicProjectId);
        }

        @Test
        void getProjectInfo_NotMemberButHasPendingInteractionAndPublicProject_ReturnsDto() {
                Long loggedInUserId = pendingUserInPublic.getId();
                Long projectid = publicProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                PublicProjectDto resultDto = projectService.getProjectInfo(projectid);

                assertEquals(true, resultDto.isHasPendingRequest());
                assertEquals(null, resultDto.getProjectRole());
                assertEquals(null, resultDto.getMemberSince());
                verify(projectRepo, times(1)).findById(publicProject.getId());
                verify(projectMemberRepo, times(0)).existsByUserIdAndProjectId(loggedInUserId,
                                publicProject.getId());
                verify(projectMemberRepo, times(1)).findByUserIdAndProjectId(loggedInUserId, projectid);
        }

        @Test
        void getOwnerProjectInfo_NotOwner_ThrowsException() {
                Long loggedInUserId = ownerUserInPrivate.getId();
                Long projectId = publicProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.getOwnerProjectInfo(projectId));
                assertEquals("You are not the owner and cannot perform this action.", e.getMessage());
        }

        @Test
        void getOwnerProjectInfo_InvalidProject_ThrowsException() {
                Long loggedInUserId = ownerUserInPrivate.getId();
                Long projectId = 1234L;
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.getOwnerProjectInfo(projectId));
        }

        @Test
        void getOwnerProjectInfo_IsOwner_ReturnsDto() {
                Long loggedInUserId = ownerUserInPublic.getId();
                Long projectId = publicProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                ProjectConfigDto result = projectService.getOwnerProjectInfo(projectId);
                assertNotNull(result, "The result should not be null");
                assertEquals(publicProject.getConfig().getId(), result.getConfigId());
                assertEquals(publicProject.getId(), result.getProjectId());
                assertEquals(true, result.getIsPublic());
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
        }

        @Test
        void getPublicProjects_Valid_ReturnsDto() {
                Page<Project> projectPage = new PageImpl<>(Collections.singletonList(publicProject));
                when(projectRepo.countAllByConfigIsPublicTrue()).thenReturn(1L);
                when(projectRepo.findAllByConfigIsPublicTrue(any(Pageable.class))).thenReturn(projectPage);

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
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.getProjectMembers(privateProjectId, 1, 10, "asc"));
                assertEquals("Only members of the project can perform this action", e.getMessage());
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
        void createNewProject_MaxLimitExceeded_WithoutPremium_ThrowsException() {
                Long loggedInUserId = ownerUserInPublic.getId();
                AcceptProjectDto dto = new AcceptProjectDto("New Project", "Description of the project", true, 10);

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(premiumAccountService.userHasActivePremiumAccount(loggedInUserId)).thenReturn(false);
                when(projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER))
                                .thenReturn(6L);
                MaxProjectOwnerLimitExceededException e = assertThrows(MaxProjectOwnerLimitExceededException.class,
                                () -> projectService.createNewProject(dto));
                assertEquals("Without premium account you can own up to 5 projects only!", e.getMessage());
                verify(projectRepo, times(0)).save(any(Project.class));
                verify(configRepo, times(0)).save(any(ProjectConfiguration.class));
                verify(projectMemberRepo, times(0)).save(any(ProjectMember.class));
        }

        @Test
        void createNewProject_MaxLimitExceeded_WithPremium_ThrowsException() {
                Long loggedInUserId = ownerUserInPublic.getId();
                AcceptProjectDto dto = new AcceptProjectDto("New Project", "Description of the project", true, 10);

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(premiumAccountService.userHasActivePremiumAccount(loggedInUserId)).thenReturn(true);
                when(projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER))
                                .thenReturn(11L);
                MaxProjectOwnerLimitExceededException e = assertThrows(MaxProjectOwnerLimitExceededException.class,
                                () -> projectService.createNewProject(dto));
                assertEquals("You have already reached your 10 accounts per premium account limit.", e.getMessage());
                verify(projectRepo, times(0)).save(any(Project.class));
                verify(configRepo, times(0)).save(any(ProjectConfiguration.class));
                verify(projectMemberRepo, times(0)).save(any(ProjectMember.class));
        }

        @Test
        void createNewProject_Success_WithoutPremium_ReturnsDto() {
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
                when(premiumAccountService.userHasActivePremiumAccount(loggedInUserId)).thenReturn(false);
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
        void createNewProject_Success_WithPremium_ReturnsDto() {
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
                when(premiumAccountService.userHasActivePremiumAccount(loggedInUserId)).thenReturn(true);
                when(projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER))
                                .thenReturn(9L);

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
                Long projectId = publicProject.getId();

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
        void makeProjectApplication_ApplicationToPrivateProject_ThrowsException() {
                Long projectId = privateProject.getId();
                when(projectRepo.existsByIdAndConfigIsPublicTrue(projectId)).thenReturn(false);
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.makeProjectApplication(projectId));
                assertEquals("Users are not allowed to send applications to private projects.", e.getMessage());
        }

        @Test
        void makeProjectApplication_ApplicationWhenAlreadyMember_ThrowsException() {
                Long projectId = publicProject.getId();
                when(projectRepo.existsByIdAndConfigIsPublicTrue(projectId)).thenReturn(true);
                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());
                UserAlreadyMemberException e = assertThrows(UserAlreadyMemberException.class,
                                () -> projectService.makeProjectApplication(projectId));
                assertEquals("You are already a member of this project.", e.getMessage());
        }

        @Test
        void makeProjectApplication_ApplicationWhenAlreadyPending_ThrowsException() {
                Long projectId = publicProject.getId();
                when(projectRepo.existsByIdAndConfigIsPublicTrue(projectId)).thenReturn(true);
                when(userService.getLoggedInUserId()).thenReturn(pendingUserInPublic.getId());

                AlreadyExistsException e = assertThrows(AlreadyExistsException.class,
                                () -> projectService.makeProjectApplication(projectId));
                assertEquals("You already have a pending invitiation / application.", e.getMessage());
        }

        @Test
        void makeProjectApplication_Valid_ReturnsDto() {
                Long projectId = publicProject.getId();
                Long loggedInUserId = ownerUserInPrivate.getId();
                when(projectRepo.existsByIdAndConfigIsPublicTrue(projectId)).thenReturn(true);
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(interactionRepo.existsByUserIdAndProjectIdAndStatus(loggedInUserId, projectId,
                                ProjectInteraction.Status.PENDING)).thenReturn(false);
                BasicMessageDto result = projectService.makeProjectApplication(projectId);
                assertNotNull(result);
                verify(interactionRepo, times(1)).save(any(ProjectInteraction.class));
                verify(notificationRepo, times(1)).save(any(Notification.class));
                assertEquals("Project application has been succesfully sent.", result.getMessage());
        }

        @Test
        void makeProjectInvitation_NotOwner_ThrowsError() {
                Long projectId = publicProject.getId();
                Long loggedInUserId = ownerUserInPrivate.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.makeProjectInvitation(projectId, null));
                assertEquals("You are not the owner and cannot perform this action.", e.getMessage());
        }

        @Test
        void makeProjectInvitation_MaxParticipantsLimit_ThrowsError() {
                Long projectId = publicProject.getId();
                Long loggedInUserId = ownerUserInPublic.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                ProjectConfiguration config = ProjectConfiguration.builder().maxParticipants(1).build();
                Project project = Project.builder().config(config).members(List.of(ProjectMember.builder().build()))
                                .build();

                when(projectRepo.findById(projectId)).thenReturn(Optional.of(project));

                MaxParticipantsReachedException e = assertThrows(MaxParticipantsReachedException.class,
                                () -> projectService.makeProjectInvitation(projectId, null));
                assertEquals("Cannot add more participants. Maximum limit reached.", e.getMessage());
        }

        @Test
        void makeProjectInvitation_Valid_ReturnsDto() {
                Long projectId = publicProject.getId();
                Long loggedInUserId = ownerUserInPublic.getId();
                InvitationDto dto = new InvitationDto("user1@test.com");
                User newUser = User.builder().id(123L).build();

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                when(userRepo.findUserByEmail(dto.getEmail())).thenReturn(Optional.of(newUser));
                when(projectMemberRepo.existsByUserIdAndProjectId(newUser.getId(), projectId)).thenReturn(false);
                when(interactionRepo.existsByUserIdAndProjectIdAndStatus(newUser.getId(), projectId,
                                ProjectInteraction.Status.PENDING)).thenReturn(false);

                BasicMessageDto result = projectService.makeProjectInvitation(projectId, dto);

                assertNotNull(result);
                assertEquals("Project invitation has been succesfully sent.", result.getMessage());
                verify(interactionRepo, times(1)).save(any(ProjectInteraction.class));
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

        @Test
        void acceptApplication_InvitationIsPassedIn_ThrowsException() {
                Long applicationid = pendingInPrivate.getId();
                InvalidInteractionException e = assertThrows(InvalidInteractionException.class,
                                () -> projectService.acceptApplication(applicationid));
                assertEquals("This interaction is not an application.", e.getMessage());
        }

        @Test
        void acceptApplication_Valid_ReturnsDto() {
                Long applicationid = pendingInPublic.getId();
                Long loggedInUserId = ownerUserInPublic.getId();

                when(projectMemberRepo.existsByUserIdAndProjectId(pendingInPublic.getUser().getId(),
                                pendingInPublic.getProject().getId())).thenReturn(false);
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                BasicMessageDto result = projectService.acceptApplication(applicationid);
                assertNotNull(result);
                assertEquals("User application to join the project has been accepted", result.getMessage());

                verify(interactionRepo, times(1)).save(any(ProjectInteraction.class));
                verify(projectMemberRepo, times(1)).save(any(ProjectMember.class));
                verify(notificationRepo, times(1)).save(any(Notification.class));
        }

        @Test
        void declineApplication_NotOwner_ThrowsException() {
                Long applicationid = pendingInPublic.getId();
                Long loggedInUserId = ownerUserInPrivate.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                assertThrows(ForbiddenException.class, () -> projectService.declineApplication(applicationid));
        }

        @Test
        void declineApplication_Valid_ReturnsDto() {
                Long applicationid = pendingInPublic.getId();
                Long loggedInUserId = ownerUserInPublic.getId();

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                BasicMessageDto result = projectService.declineApplication(applicationid);

                assertNotNull(result);
                assertEquals("User application to join the project has been declined", result.getMessage());
                assertEquals(pendingInPublic.getStatus(), ProjectInteraction.Status.DECLINED);

                verify(interactionRepo, times(1)).save(any(ProjectInteraction.class));
                verify(projectMemberRepo, times(0)).save(any(ProjectMember.class));
                verify(notificationRepo, times(1)).save(any(Notification.class));
        }

        @Test
        void acceptInvitation_OtherUser_ThrowsException() {
                Long invitationId = pendingInPrivate.getId();
                Long loggedInUserId = pendingUserInPublic.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.acceptInvitation(invitationId));
                assertEquals("Users are not allowed to manage other user's invitations", e.getMessage());
        }

        @Test
        void acceptInvitation_Valid_ReturnsDto() {
                Long invitationId = pendingInPrivate.getId();
                Long loggedInUserId = pendingUserInPrivate.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                BasicMessageDto result = projectService.acceptInvitation(invitationId);
                assertNotNull(result);
                assertEquals("Project invitation has succefully been accepted.", result.getMessage());
                assertEquals(pendingInPrivate.getStatus(), ProjectInteraction.Status.ACCEPTED);

                verify(interactionRepo, times(1)).save(any(ProjectInteraction.class));
                verify(projectMemberRepo, times(1)).save(any(ProjectMember.class));
                verify(notificationRepo, times(1)).save(any(Notification.class));
        }

        @Test
        void declineInvitation_OtherUser_ThrowsException() {
                Long invitationId = pendingInPrivate.getId();
                Long loggedInUserId = pendingUserInPublic.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.declineInvitation(invitationId));
                assertEquals("Users are not allowed to manage other user's invitations", e.getMessage());
        }

        @Test
        void declinenvitation_Valid_ReturnsDto() {
                Long invitationId = pendingInPrivate.getId();
                Long loggedInUserId = pendingUserInPrivate.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                BasicMessageDto result = projectService.declineInvitation(invitationId);
                assertNotNull(result);
                assertEquals("Project invitation has succefully been declined.", result.getMessage());
                assertEquals(pendingInPrivate.getStatus(), ProjectInteraction.Status.DECLINED);

                verify(interactionRepo, times(1)).save(any(ProjectInteraction.class));
                verify(projectMemberRepo, times(0)).save(any(ProjectMember.class));
                verify(notificationRepo, times(1)).save(any(Notification.class));
        }

        @Test
        void cancelApplication_WrongUser_ThrowsException() {
                Long applicationId = pendingInPublic.getId();
                Long loggedInUserId = pendingInPrivate.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.cancelApplication(applicationId));
                assertEquals("Users are not allowed to manage other user's applications", e.getMessage());
        }

        @Test
        void cancelApplication_Valid_ReturnsDto() {
                Long applicationId = pendingInPublic.getId();
                Long loggedInUserId = pendingUserInPublic.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                BasicMessageDto result = projectService.cancelApplication(applicationId);
                assertNotNull(result);
                assertEquals("Project application has succefully been canceled.", result.getMessage());

                verify(interactionRepo, times(1)).delete(any(ProjectInteraction.class));
        }

        @Test
        void cancelInvitation_NotOwner_ThrowsException() {
                Long invitationid = pendingInPrivate.getId();
                Long loggedInUserId = ownerMemberInPublic.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.cancelInvitation(invitationid));
                assertEquals("You are not the owner and cannot perform this action.", e.getMessage());
        }

        @Test
        void cancelInvitation_Valid_ReturnsDto() {
                Long invitationid = pendingInPrivate.getId();
                Long loggedInUserId = ownerUserInPrivate.getId();
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                BasicMessageDto result = projectService.cancelInvitation(invitationid);
                assertNotNull(result);
                assertEquals("Project invitation has been succefully canceled.", result.getMessage());

                verify(interactionRepo, times(1)).delete(any(ProjectInteraction.class));
        }

        @Test
        void kickMemberOut_TryToKickOwner_ThrowsException() {
                Long projectMemberId = ownerMemberInPublic.getId();
                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.kickMemberOut(projectMemberId));
                assertEquals("It is imposiible to kick the owner of the project out.", e.getMessage());

        }

        @Test
        void kickMemberOut_NotOwnerTryingToCallEndpoint_ThrowsException() {
                Long projectMemberId = 2L;
                Long loggedInUserId = ownerUserInPrivate.getId();

                when(projectMemberRepo.findById(projectMemberId))
                                .thenReturn(Optional.of(ProjectMember.builder().project(publicProject).build()));

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.kickMemberOut(projectMemberId));
                assertEquals("You are not the owner and cannot perform this action.", e.getMessage());
        }

        @Test
        void kickMemberOut_Valid_ReturnsDto() {
                Long projectMemberId = 2L;
                Long loggedInUserId = ownerUserInPublic.getId();

                when(projectMemberRepo.findById(projectMemberId))
                                .thenReturn(Optional.of(ProjectMember.builder().project(publicProject).build()));

                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                BasicMessageDto result = projectService.kickMemberOut(projectMemberId);
                assertNotNull(result);
                assertEquals("Project member has been successfully excluded from the project.", result.getMessage());

                verify(projectMemberRepo, times(1)).deleteById(projectMemberId);
                verify(notificationRepo, times(1)).save(any(Notification.class));
        }

        @Test
        void leaveProject_OwnerTriesToLeave_ThrowsException() {
                Long projectId = publicProject.getId();
                when(userService.getLoggedInUserId()).thenReturn(ownerUserInPublic.getId());

                ForbiddenException e = assertThrows(ForbiddenException.class,
                                () -> projectService.leaveProject(projectId));
                assertEquals("Owner cannot leave the project. Consider deleting it.", e.getMessage());
        }

        @Test
        void leaveProject_Valid_ReturnsDto() {
                Long projectId = publicProject.getId();

                User anotherUserInTheProject = User.builder().id(312L).firstName("For").lastName("Test").build();
                Long loggedInUserId = ownerMemberInPublic.getId();

                when(projectMemberRepo.findByUserIdAndProjectId(loggedInUserId, publicProject.getId()))
                                .thenReturn(Optional.of(ProjectMember.builder().project(publicProject)
                                                .user(anotherUserInTheProject).build()));
                when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

                BasicMessageDto result = projectService.leaveProject(projectId);
                assertNotNull(result);
                assertEquals("You have succesfully left the project.", result.getMessage());

                verify(projectMemberRepo, times(1)).delete(any(ProjectMember.class));
                verify(notificationRepo, times(1)).save(any(Notification.class));
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