package com.accenture.backend.entity;

import java.time.*;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_members")
@Getter
@Setter
@NoArgsConstructor
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole projectRole;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectDiscussion> discussions = new ArrayList<>();

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectDiscussionMessage> discussionMessages = new ArrayList<>();

    public static enum ProjectRole {
        OWNER, MANAGER, USER
    }

    @Builder
    public ProjectMember(User user, Project project, ProjectRole projectRole, LocalDateTime joinDate) {
        this.user = user;
        this.project = project;
        this.projectRole = (projectRole != null) ? projectRole : ProjectRole.USER;
        this.joinDate = joinDate;
    }

    @PrePersist
    public void onPrePersist() {
        if (joinDate == null) {
            joinDate = LocalDateTime.now();
        }
    }

}