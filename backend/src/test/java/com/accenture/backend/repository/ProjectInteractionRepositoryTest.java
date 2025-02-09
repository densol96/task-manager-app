package com.accenture.backend.repository;

import com.accenture.backend.entity.ProjectInteraction;
import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;
import com.accenture.backend.entity.Project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProjectInteractionRepositoryTest {

        @Autowired
        private ProjectInteractionRepository interactionRepo;

        @Autowired
        private UserRepository userRepo;

        @Autowired
        private ProjectRepository projectRepo;

        private User user;
        private Project project;

        @BeforeEach
        void setUp() {
                user = userRepo.save(User.builder()
                                .email("john.doe@example.com")
                                .firstName("John")
                                .lastName("Doe")
                                .password("securePass123")
                                .role(Role.USER)
                                .build());
                project = projectRepo
                                .save(Project.builder().title("Test project").description("Test description").build());

                ProjectInteraction interaction1 = ProjectInteraction.builder()
                                .user(user)
                                .project(project)
                                .type(ProjectInteraction.Type.INVITATION)
                                .status(ProjectInteraction.Status.PENDING)
                                .initComment("Init test comment1")
                                .build();

                ProjectInteraction interaction2 = ProjectInteraction.builder()
                                .user(user)
                                .project(project)
                                .type(ProjectInteraction.Type.APPLICATION)
                                .status(ProjectInteraction.Status.PENDING)
                                .initComment("Init test comment2")
                                .build();

                ProjectInteraction interaction3 = ProjectInteraction.builder()
                                .user(user)
                                .project(project)
                                .type(ProjectInteraction.Type.INVITATION)
                                .status(ProjectInteraction.Status.DECLINED)
                                .initComment("Init test comment")
                                .build();

                interactionRepo.saveAll(Arrays.asList(interaction1, interaction2, interaction3));
        }

        @Test
        public void findAllByUserIdAndTypeAndStatus_ReturnsPendingInvitation() {
                List<ProjectInteraction> results = interactionRepo.findAllByUserIdAndTypeAndStatus(
                                user.getId(), ProjectInteraction.Type.INVITATION, ProjectInteraction.Status.PENDING);

                assertThat(results).hasSize(1);

                ProjectInteraction result = results.get(0);
                assertThat(result.getType()).isEqualTo(ProjectInteraction.Type.INVITATION);
                assertThat(result.getStatus()).isEqualTo(ProjectInteraction.Status.PENDING);
        }

        @Test
        public void findAllByUserIdAndTypeAndStatus_ReturnsEmptyList_WhenNoMatchingInvitation() {
                List<ProjectInteraction> results = interactionRepo.findAllByUserIdAndTypeAndStatus(
                                user.getId(), ProjectInteraction.Type.INVITATION, ProjectInteraction.Status.ACCEPTED);

                assertThat(results).isEmpty();
        }

        @Test
        public void findAllByProjectIdAndTypeAndStatus_ReturnsPendingApplication() {
                List<ProjectInteraction> results = interactionRepo.findAllByProjectIdAndTypeAndStatus(
                                project.getId(), ProjectInteraction.Type.APPLICATION,
                                ProjectInteraction.Status.PENDING);

                assertThat(results).hasSize(1);
                ProjectInteraction result = results.get(0);

                assertThat(result.getProject()).isEqualTo(project);
                assertThat(result.getType()).isEqualTo(ProjectInteraction.Type.APPLICATION);
                assertThat(result.getStatus()).isEqualTo(ProjectInteraction.Status.PENDING);
        }

        @Test
        public void findAllByProjectIdAndTypeAndStatus_ReturnsEmptyList_WhenNoMatchingApplication() {
                List<ProjectInteraction> results = interactionRepo.findAllByProjectIdAndTypeAndStatus(
                                project.getId(), ProjectInteraction.Type.APPLICATION,
                                ProjectInteraction.Status.ACCEPTED);

                assertThat(results).isEmpty();
        }

        @Test
        public void existsByUserIdAndProjectIdAndStatus_NoAcceptedInteractionExists() {
                Boolean exists1 = interactionRepo.existsByUserIdAndProjectIdAndStatus(
                                user.getId(), project.getId(), ProjectInteraction.Status.ACCEPTED);

                assertThat(exists1).isFalse();
        }

        @Test
        public void existsByUserIdAndProjectIdAndStatus_NoDeclinedInteractionExists() {
                Boolean exists = interactionRepo.existsByUserIdAndProjectIdAndStatus(
                                user.getId(), project.getId(), ProjectInteraction.Status.DECLINED);

                assertThat(exists).isTrue();
        }

        @Test
        public void existsByUserIdAndProjectIdAndStatus_NoPendingInteractionExists() {
                Boolean exists = interactionRepo.existsByUserIdAndProjectIdAndStatus(
                                user.getId(), project.getId(), ProjectInteraction.Status.PENDING);

                assertThat(exists).isTrue();
        }

        @Test
        public void findAllByUserIdAndTypeAndStatus_ReturnsOnlyInteractionsForSpecificUser() {
                Project project2 = projectRepo
                                .save(Project.builder().title("Another project").description("Another description")
                                                .build());

                ProjectInteraction interaction4 = ProjectInteraction.builder()
                                .user(user)
                                .project(project2)
                                .type(ProjectInteraction.Type.INVITATION)
                                .status(ProjectInteraction.Status.PENDING)
                                .initComment("Test comment for project2")
                                .build();

                interactionRepo.save(interaction4);

                List<ProjectInteraction> results = interactionRepo.findAllByUserIdAndTypeAndStatus(
                                user.getId(), ProjectInteraction.Type.INVITATION, ProjectInteraction.Status.PENDING);

                assertThat(results).hasSize(2);

                for (ProjectInteraction interaction : results) {
                        assertThat(interaction.getUser()).isEqualTo(user);
                        assertThat(interaction.getType()).isEqualTo(ProjectInteraction.Type.INVITATION);
                        assertThat(interaction.getStatus()).isEqualTo(ProjectInteraction.Status.PENDING);
                }
        }
}
