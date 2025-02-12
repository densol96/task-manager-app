package com.accenture.backend.repository;

import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class ProjectMemberRepositoryTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    private Long projectMemberId;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("securePass123")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user1);

        Project project1 = new Project("Project 1", "Description for Project 1", LocalDateTime.now());
        Project savedProject1 = projectRepository.save(project1);

        ProjectMember projectMember = new ProjectMember(savedUser, savedProject1, ProjectMember.ProjectRole.USER, LocalDateTime.now());
        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);

        projectMemberId = savedProjectMember.getId();
    }

    @AfterEach
    void tearDown() {
        projectMemberRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindById_Exists() {
        Optional<ProjectMember> projectMemberOptional = projectMemberRepository.findById(projectMemberId);
        assertTrue(projectMemberOptional.isPresent());
    }

    @Test
    void testFindById_NotExists() {
        Optional<ProjectMember> projectMemberOptional = projectMemberRepository.findById(999L);
        assertFalse(projectMemberOptional.isPresent());
    }

    @Test
    void testSaveProjectMember() {
        User user1 = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("securePass123")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user1);

        Project project = new Project("New Project", "Description for new project", LocalDateTime.now());
        projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember(savedUser, project, ProjectMember.ProjectRole.MANAGER, LocalDateTime.now());
        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);

        assertNotNull(savedProjectMember.getId());
    }
}

