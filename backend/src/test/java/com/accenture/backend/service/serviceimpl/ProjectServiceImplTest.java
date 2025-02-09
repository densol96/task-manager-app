package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.dto.request.AcceptProjectDto;
import com.accenture.backend.dto.response.BasicNestedResponseDto;
import com.accenture.backend.dto.response.ProjectDto;
import com.accenture.backend.dto.response.PublicProjectDto;
import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectConfiguration;
import com.accenture.backend.entity.ProjectInteraction;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.User;
import com.accenture.backend.exception.custom.AuthenticationRuntimeException;
import com.accenture.backend.exception.custom.InvalidInputException;
import com.accenture.backend.exception.custom.MaxProjectOwnerLimitExceededException;
import com.accenture.backend.exception.custom.PageOutOfRangeException;
import com.accenture.backend.repository.ProjectRepository;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.service.UserService;
import com.accenture.backend.repository.ProjectConfigurationRepository;
import com.accenture.backend.repository.ProjectInteractionRepository;
import com.accenture.backend.repository.ProjectMemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.lang.IllegalAccessException;
import java.lang.NoSuchFieldException;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepo;

    @Mock
    private ProjectMemberRepository projectMemberRepo;

    @Mock
    private ProjectConfigurationRepository configRepo;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectInteractionRepository interactionRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User loggedInuser;
    private ProjectMember loggedInuserAsOwner;
    private Project project;

    @BeforeEach
    void setUp() {
        /*
         * I have removed the opportunity to set up an id through Builder, as it is
         * auto-generated by DB, however added a setter method to ease the tests and
         * avoid using reflexions.
         */

        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(projectService, "maxProjectAmountAllowed", 5);

        loggedInuser = User.builder().firstName("Solovjovs").firstName("Deniss").email("solo@test.com").build();
        loggedInuser.setId(1L);

        project = Project.builder()
                .title("Test project").description("Test description").build();
        project.setId(1L);

        loggedInuserAsOwner = ProjectMember.builder().user(loggedInuser).project(project)
                .projectRole(ProjectMember.Role.OWNER)
                .build();
        loggedInuserAsOwner.setId(1L);

        ProjectConfiguration config = ProjectConfiguration.builder().maxParticipants(dto.getMaxParticipants())
                .isPublic(dto.getIsPublic()).project(newProject).build();

    }

    @Test
    public void getPublicProjects_ReturnsCorrectDto() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        Long loggedInUserId = loggedInuser.getId();
        Integer page = 1;
        Integer size = 5;
        String sortBy = "createdAt";
        String sortDirection = "asc";
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.asc(sortBy)));

        when(projectRepo.countAllByConfigIsPublicTrue()).thenReturn(1L);
        when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
        when(projectRepo.findAllByConfigIsPublicTrue(eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(project), pageable, 1));
        when(projectRepo.existsById(project.getId())).thenReturn(true);
        when(projectMemberRepo.findByProjectIdAndProjectRole(project.getId(),
                ProjectMember.Role.OWNER)).thenReturn(Arrays.asList(owner));
        when(projectMemberRepo.existsByUserIdAndProjectId(loggedInUserId, project.getId())).thenReturn(true);
        when(interactionRepo.existsByUserIdAndProjectIdAndStatus(loggedInUserId, project.getId(),
                ProjectInteraction.Status.PENDING)).thenReturn(false);

        // Act
        Page<PublicProjectDto> result = projectService.getPublicProjects(page, size, sortBy, sortDirection);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        PublicProjectDto dto = result.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(project.getId());
        assertThat(dto.getTitle()).isEqualTo(project.getTitle());
        assertThat(dto.getDescription()).isEqualTo(project.getDescription());
        assertThat(dto.isMember()).isTrue();
        assertThat(dto.isHasPendingRequest()).isFalse();

        Pageable pageableResult = result.getPageable();
        assertThat(pageableResult.getSort().toString()).contains(sortBy);
        assertThat(pageableResult.getSort().toString()).contains(sortDirection.toUpperCase());

        verify(projectRepo, times(1)).countAllByConfigIsPublicTrue();
        verify(projectRepo, times(1)).findAllByConfigIsPublicTrue(any(Pageable.class));
    }

    @Test
    void getPublicProjects_ThrowsException_InvalidInput_Page() {
        int invalidPage = -1;
        int size = 10;
        String sortBy = "date";
        String sortDirection = "desc";

        when(projectRepo.countAllByConfigIsPublicTrue()).thenReturn(1L);

        assertThrows(InvalidInputException.class, () -> {
            projectService.getPublicProjects(invalidPage, size, sortBy, sortDirection);
        });
    }

    @Test
    void getPublicProjects_ThrowsException_InvalidInput_Size() {
        int page = 1;
        int invalidSize = 0;
        String sortBy = "date";
        String sortDirection = "desc";

        assertThrows(InvalidInputException.class, () -> {
            projectService.getPublicProjects(page, invalidSize, sortBy, sortDirection);
        });
    }

    @Test
    void getPublicProjects_ThrowsException_PageOutOfRange() {
        int page = 2;
        int size = 10;
        String sortBy = "date";
        String sortDirection = "desc";

        when(projectRepo.countAllByConfigIsPublicTrue()).thenReturn(5L);

        assertThrows(PageOutOfRangeException.class, () -> {
            projectService.getPublicProjects(page, size, sortBy, sortDirection);
        });
    }

    @Test
    void getPublicProjects_ThrowsException_InvalidInput_SortBy() {
        int page = 1;
        int size = 10;
        String invalidSortBy = "invalidField";
        String sortDirection = "desc";

        when(projectRepo.countAllByConfigIsPublicTrue()).thenReturn(5L);

        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> {
            projectService.getPublicProjects(page, size, invalidSortBy, sortDirection);
        });

        assertEquals("Projects can only be sorted by either date or title.", thrown.getMessage());
    }

    @Test
    void createNewProject_Success() {
        Long loggedInUserId = loggedInuser.getId();
        AcceptProjectDto dto = new AcceptProjectDto("New Project", "Description", true, 10);

        when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
        when(projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER))
                .thenReturn(0L);

        when(projectRepo.save(any(Project.class))).thenReturn(newProject);
        when(configRepo.save(any(ProjectConfiguration.class))).thenReturn(new ProjectConfiguration());
        when(userRepository.findById(loggedInUserId)).thenReturn(Optional.of(user));
        when(projectMemberRepo.save(any(ProjectMember.class))).thenReturn(owner);
        when(projectRepo.existsById(newProject.getId())).thenReturn(true);
        when(projectMemberRepo.findByProjectIdAndProjectRole(newProject.getId(),
                ProjectMember.Role.OWNER)).thenReturn(Arrays.asList(owner));

        // Act
        BasicNestedResponseDto<ProjectDto> response = projectService.createNewProject(dto);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("New project has been succefully created");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getProjectInfo()).isNotNull();
        assertThat(response.getData().getConfig()).isNotNull();
        assertThat(response.getData().getConfig().getMaxParticipants()).isEqualTo(dto.getMaxParticipants());
        assertThat(response.getData().getConfig().getIsPublic()).isEqualTo(dto.getIsPublic());
        assertThat(response.getData().getProjectInfo().getTitle()).isEqualTo(dto.getTitle());

        verify(projectRepo, times(1)).save(any(Project.class));
        verify(configRepo, times(1)).save(any(ProjectConfiguration.class));
        verify(projectMemberRepo, times(1)).save(any(ProjectMember.class));
        verify(projectMemberRepo, times(1)).countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER);
        verify(userRepository, times(1)).findById(loggedInUserId);
    }

    @Test
    void createNewProject_MaxProjectLimitExceeded() {
        Long loggedInUserId = 1L;
        AcceptProjectDto dto = new AcceptProjectDto("New Project", "Description", true, 10);

        when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
        when(projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER))
                .thenReturn(10L);

        assertThrows(MaxProjectOwnerLimitExceededException.class, () -> {
            projectService.createNewProject(dto);
        });
    }

    @Test
    void createNewProject_UserNotFound() {
        Long loggedInUserId = 1L;
        AcceptProjectDto dto = new AcceptProjectDto("New Project", "Description", true, 10);

        when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);
        when(projectMemberRepo.countAllByUserIdAndProjectRole(loggedInUserId, ProjectMember.Role.OWNER))
                .thenReturn(0L);

        assertThrows(AuthenticationRuntimeException.class, () -> {
            projectService.createNewProject(dto);
        });
    }

    @Test
    void updateExistingProject_Success() {
        // Arrange
        Long loggedInUserId = 1L;
        Long projectId = 1L;

        AcceptProjectDto dto = new AcceptProjectDto("Updated Title", "Updated Description", true, 15);

        Project existingProject = Project.builder()
                .title("Old Title")
                .description("Old Desc")
                .build();
        existingProject.setId(projectId);

        ProjectConfiguration existingConfig = ProjectConfiguration.builder()
                .isPublic(false)
                .maxParticipants(10)
                .build();
        existingProject.setConfig(existingConfig);

        User user = User.builder()
                .id(loggedInUserId)
                .firstName("Solovjovs")
                .lastName("Deniss")
                .email("solo@test.com")
                .build();

        ProjectMember owner = ProjectMember.builder()
                .user(user)
                .project(existingProject)
                .projectRole(ProjectMember.Role.OWNER)
                .build();

        when(projectRepo.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectMemberRepo.findByProjectIdAndProjectRole(projectId, ProjectMember.Role.OWNER))
                .thenReturn(Arrays.asList(owner));
        when(projectRepo.save(any(Project.class))).thenReturn(existingProject);
        when(projectRepo.existsById(projectId)).thenReturn(true);
        when(userService.getLoggedInUserId()).thenReturn(loggedInUserId);

        BasicNestedResponseDto<ProjectDto> response = projectService.updateExistingProject(projectId, dto);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Existing project has been succefully updated");
        assertThat(response.getData().getProjectInfo().getTitle()).isEqualTo(dto.getTitle());
        assertThat(response.getData().getProjectInfo().getDescription()).isEqualTo(dto.getDescription());
        assertThat(response.getData().getConfig().getIsPublic()).isEqualTo(dto.getIsPublic());
        assertThat(response.getData().getConfig().getMaxParticipants()).isEqualTo(dto.getMaxParticipants());

        verify(projectRepo, times(1)).save(existingProject);
    }

}