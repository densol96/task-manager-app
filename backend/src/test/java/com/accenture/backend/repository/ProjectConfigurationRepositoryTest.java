package com.accenture.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProjectConfigurationRepositoryTest {
    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private ProjectConfigurationRepository configRepo;

    private Project project;
    private ProjectConfiguration config;

    @BeforeEach
    void setUp() {
        project = projectRepo.save(Project.builder().title("Test project").build());

        config = configRepo
                .save(ProjectConfiguration.builder().project(project).isPublic(true).build());

        // Additional project and config
        Project projectNew = projectRepo.save(Project.builder().title("Test project2").build());
        ProjectConfiguration configNew = configRepo
                .save(ProjectConfiguration.builder().project(projectNew).isPublic(true).build());
    }

    @Test
    public void findByProjectId_ReturnsConfig() {
        ProjectConfiguration config = configRepo.findByProjectId(project.getId());
        assertThat(config).isNotNull();
        assertThat(config.getId()).isEqualTo(config.getId());
        assertThat(config.getIsPublic()).isEqualTo(config.getIsPublic());
        assertThat(config.getProject().getTitle()).isEqualTo(project.getTitle());
    }

    @Test
    public void findByProjectId_ReturnsNull() {
        ProjectConfiguration config = configRepo.findByProjectId(1234L);
        assertThat(config).isNull();
    }
}
