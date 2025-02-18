package com.accenture.backend.repository;

import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectConfiguration;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectConfigurationRepository configRepo;

    @Autowired
    private ProjectMemberRepository projectMemberRepo;

    private User user;
    private Project publicProject;
    private Project privateProject;

    @BeforeEach
    void setUp() {
        user = userRepo.save(User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("securePass123")
                .role(Role.USER)
                .build());

        publicProject = projectRepo.save(Project.builder()
                .title("Public Project")
                .description("A public project")
                .build());
        configRepo.save(ProjectConfiguration.builder().project(publicProject).isPublic(true).build());

        privateProject = projectRepo.save(Project.builder()
                .title("Private Project")
                .description("A private project")
                .build());
        configRepo.save(ProjectConfiguration.builder().project(privateProject).isPublic(false).build());

        projectMemberRepo.save(ProjectMember.builder().user(user).project(privateProject).build());
        projectMemberRepo.save(ProjectMember.builder().user(user).project(publicProject).build());
    }

    @AfterEach
    void tearDown() {
        projectMemberRepo.deleteAll();
        configRepo.deleteAll();
        projectRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    public void existsByIdAndConfigIsPublicTrue_ResultIsTrue() {
        Boolean result = projectRepo.existsByIdAndConfigIsPublicTrue(publicProject.getId());
        assertThat(result).isTrue();
    }

    @Test
    public void existsByIdAndConfigIsPublicTrue_ResultIsFalse() {
        Boolean result = projectRepo.existsByIdAndConfigIsPublicTrue(privateProject.getId());
        assertThat(result).isFalse();
    }

    @Test
    public void countAllByConfigIsPublicTrue_ReturnsCorrectCount() {
        Long count = projectRepo.countAllByConfigIsPublicTrue();
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void findAllByConfigIsPublicTrue_ReturnsPagedProjects() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());

        Page<Project> result = projectRepo.findAllByConfigIsPublicTrue(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(publicProject.getTitle());
        assertThat(result.getContent().get(0).getDescription()).isEqualTo(publicProject.getDescription());
    }

    @Test
    public void findProjectsByUserId_ReturnsPagedProjectsForUser() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());

        Page<Project> result = projectRepo.findProjectsByUserId(user.getId(), pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    public void findProjectsByUserId_ReturnsEmptyPageIfNoProjects() {
        User otherUser = userRepo.save(User.builder()
                .email("another.user@example.com")
                .firstName("Another")
                .lastName("User")
                .password("securePass123")
                .role(Role.USER)
                .build());

        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());
        Page<Project> result = projectRepo.findProjectsByUserId(otherUser.getId(), pageable);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }
}
