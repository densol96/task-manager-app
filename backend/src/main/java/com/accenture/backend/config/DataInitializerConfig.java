package com.accenture.backend.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectConfiguration;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.repository.ProjectConfigurationRepository;
import com.accenture.backend.repository.ProjectMemberRepository;
import com.accenture.backend.repository.ProjectRepository;

@Configuration
public class DataInitializerConfig {
        @Bean
        @Profile("seed")
        public CommandLineRunner seedInitialDataInDatabase(ProjectMemberRepository memberRepo,
                        ProjectRepository projectRepo, ProjectConfigurationRepository configRepo) {
                return args -> {

                        Project project1 = Project.builder()
                                        .title("First project")
                                        .description("This is a project for testing putposes.").build();
                        Project project2 = Project.builder()
                                        .title("Second project")
                                        .description("This is a project for testing putposes.").build();
                        Project project3 = Project.builder()
                                        .title("Third project")
                                        .description("This is a project for testing putposes.").build();
                        Project project4 = Project.builder()
                                        .title("Fourth project")
                                        .description("This is a project for testing putposes.").build();

                        projectRepo.saveAll(Arrays.asList(project1, project2, project3, project4));

                        ProjectConfiguration config1 = ProjectConfiguration.builder().project(project1).isPublic(true)
                                        .maxParticipants(5).build();

                        ProjectConfiguration config2 = ProjectConfiguration.builder().project(project2).isPublic(false)
                                        .maxParticipants(6).build();

                        ProjectConfiguration config3 = ProjectConfiguration.builder().project(project3).isPublic(true)
                                        .maxParticipants(7).build();

                        ProjectConfiguration config4 = ProjectConfiguration.builder().project(project4).isPublic(false)
                                        .maxParticipants(8).build();

                        configRepo.saveAll(Arrays.asList(config1, config2, config3, config4));

                        ProjectMember member1 = ProjectMember.builder().project(project1)
                                        .projectRole(ProjectMember.ProjectRole.OWNER).build();

                        ProjectMember member2 = ProjectMember.builder().project(project2)
                                        .projectRole(ProjectMember.ProjectRole.USER).build();

                        ProjectMember member3 = ProjectMember.builder().project(project3)
                                        .projectRole(ProjectMember.ProjectRole.OWNER).build();

                        ProjectMember member4 = ProjectMember.builder().project(project4)
                                        .projectRole(ProjectMember.ProjectRole.USER).build();

                        memberRepo.saveAll(Arrays.asList(member1, member2, member3, member4));

                        System.out.println("=== DATABASE DATA SEEDED ===");
                };
        }
}
