package com.accenture.backend.repository;

import com.accenture.backend.entity.Project;
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
class ProjectRepositoryTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private ProjectRepository projectRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    private Long projectId;

    @BeforeEach
    void setUp() {
        Project project1 = new Project("Project 1", "Description for Project 1", LocalDateTime.now());
        Project savedProject1 = projectRepository.save(project1);
        projectId = savedProject1.getId();

        Project project2 = new Project("Project 2", "Description for Project 2", LocalDateTime.now());
        projectRepository.save(project2);
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
    }

    @Test
    void testFindById_Exists() {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        assertTrue(projectOptional.isPresent(), "Project should be found");
        assertEquals("Project 1", projectOptional.get().getTitle(), "Project title should match");
    }

    @Test
    void testFindById_NotExists() {
        Optional<Project> projectOptional = projectRepository.findById(999L);
        assertFalse(projectOptional.isPresent(), "Project should not be found");
    }

    @Test
    void testSaveProject() {
        Project project = new Project("New Project", "New project description", LocalDateTime.now());
        Project savedProject = projectRepository.save(project);
        assertNotNull(savedProject.getId(), "Saved project should have a generated ID");
        assertEquals("New Project", savedProject.getTitle(), "Project title should match");
    }
}
