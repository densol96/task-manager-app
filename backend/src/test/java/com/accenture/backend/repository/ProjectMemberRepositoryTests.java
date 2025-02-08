package com.accenture.backend.repository;

import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.User;
import com.accenture.backend.entity.Project;
import com.accenture.backend.enums.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProjectMemberRepositoryTests {

    @Autowired
    private ProjectMemberRepository projectMemberRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectRepository projectRepo;

    private Long userAndOwnerId;
    private Long ownerId;
    private Long userAndUserId;
    private Long hasTwoMembersId;
    private Long hasThreeMembersId;

    @BeforeEach
    void setUp() {
        User userAndOwner = userRepo.save(User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("securePass123")
                .role(Role.USER)
                .build());
        userAndOwnerId = userAndOwner.getId();

        User owner = userRepo.save(User.builder()
                .email("not.john.doe@example.com")
                .firstName("NotJohn")
                .lastName("TotallyNotDoe")
                .password("securePass123")
                .role(Role.USER)
                .build());
        ownerId = owner.getId();

        User userAndUser = userRepo.save(User.builder()
                .email("test@test.com")
                .firstName("Test")
                .lastName("Tested")
                .password("securePass123")
                .role(Role.USER)
                .build());
        userAndUserId = userAndUser.getId();

        Project hasTwoMembers = projectRepo
                .save(Project.builder().title("Test project").description("Test description").build());
        hasTwoMembersId = hasTwoMembers.getId();

        Project hasThreeMembers = projectRepo
                .save(Project.builder().title("Test project 2").description("Test description 2").build());
        hasThreeMembersId = hasThreeMembers.getId();

        ProjectMember member1 = ProjectMember.builder().user(userAndOwner).project(hasTwoMembers)
                .projectRole(ProjectMember.Role.OWNER).build();
        ProjectMember member2 = ProjectMember.builder().user(userAndOwner).project(hasThreeMembers)
                .projectRole(ProjectMember.Role.USER).build();
        ProjectMember member3 = ProjectMember.builder().user(owner).project(hasThreeMembers)
                .projectRole(ProjectMember.Role.OWNER).build();
        ProjectMember member4 = ProjectMember.builder().user(userAndUser).project(hasTwoMembers)
                .projectRole(ProjectMember.Role.USER).build();
        ProjectMember member5 = ProjectMember.builder().user(userAndUser).project(hasThreeMembers)
                .projectRole(ProjectMember.Role.USER).build();

        projectMemberRepo.saveAll(Arrays.asList(member1, member2, member3, member4, member5));
    }

    @Test
    public void countAllByUserId_ReturnsCorrectCount() {
        assertThat(projectMemberRepo.countAllByUserId(userAndOwnerId)).isEqualTo(2);
        assertThat(projectMemberRepo.countAllByUserId(ownerId)).isEqualTo(1);
        assertThat(projectMemberRepo.countAllByUserId(userAndUserId)).isEqualTo(2);
        assertThat(projectMemberRepo.countAllByUserId(123L)).isEqualTo(0);
    }

    @Test
    public void countAllByUserIdAndProjectRole_ReturnsCorrectCount() {
        assertThat(projectMemberRepo.countAllByUserIdAndProjectRole(ownerId, ProjectMember.Role.OWNER))
                .isEqualTo(1);
        assertThat(projectMemberRepo.countAllByUserIdAndProjectRole(ownerId, ProjectMember.Role.USER))
                .isEqualTo(0);
        assertThat(projectMemberRepo.countAllByUserIdAndProjectRole(userAndOwnerId, ProjectMember.Role.OWNER))
                .isEqualTo(1);
        assertThat(projectMemberRepo.countAllByUserIdAndProjectRole(userAndOwnerId, ProjectMember.Role.USER))
                .isEqualTo(1);
        assertThat(projectMemberRepo.countAllByUserIdAndProjectRole(userAndUserId, ProjectMember.Role.USER))
                .isEqualTo(2);
        assertThat(projectMemberRepo.countAllByUserIdAndProjectRole(userAndUserId, ProjectMember.Role.OWNER))
                .isEqualTo(0);
    }

    @Test
    public void findByProjectIdAndProjectRole_ReturnsCorrectMembers() {
        assertThat(projectMemberRepo.findByProjectIdAndProjectRole(hasTwoMembersId,
                ProjectMember.Role.USER)).hasSize(1);
        assertThat(projectMemberRepo.findByProjectIdAndProjectRole(hasTwoMembersId,
                ProjectMember.Role.OWNER)).hasSize(1);

        assertThat(projectMemberRepo.findByProjectIdAndProjectRole(hasThreeMembersId,
                ProjectMember.Role.USER)).hasSize(2);
        assertThat(projectMemberRepo.findByProjectIdAndProjectRole(hasThreeMembersId,
                ProjectMember.Role.OWNER)).hasSize(1);
    }

    @Test
    public void findByUserIdAndProjectId_ReturnsCorrectMember() {
        Optional<ProjectMember> foundMember = projectMemberRepo.findByUserIdAndProjectId(userAndOwnerId,
                hasTwoMembersId);

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getUser().getId()).isEqualTo(userAndOwnerId);
        assertThat(foundMember.get().getProject().getId()).isEqualTo(hasTwoMembersId);

        assertThat(projectMemberRepo.findByUserIdAndProjectId(userAndUserId,
                hasTwoMembersId)).isPresent();

        assertThat(projectMemberRepo.findByUserIdAndProjectId(ownerId,
                hasTwoMembersId)).isEmpty();
    }

    @Test
    public void existsByUserIdAndProjectId_ReturnsTrueIfMemberExists() {
        assertThat(projectMemberRepo.existsByUserIdAndProjectId(userAndOwnerId, hasTwoMembersId)).isTrue();
        assertThat(projectMemberRepo.existsByUserIdAndProjectId(userAndOwnerId, hasThreeMembersId)).isTrue();
        assertThat(projectMemberRepo.existsByUserIdAndProjectId(ownerId, hasThreeMembersId)).isTrue();
    }

    @Test
    public void existsByUserIdAndProjectId_ReturnsFalseIfMemberDoesNotExist() {
        assertThat(projectMemberRepo.existsByUserIdAndProjectId(ownerId, hasTwoMembersId)).isFalse();
    }
}
