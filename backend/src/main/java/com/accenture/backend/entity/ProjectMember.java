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
@AllArgsConstructor
@Builder
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role projectRole = ProjectMember.Role.USER;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectDiscussion> discussions = new ArrayList<>();

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectDiscussionMessage> discussionMessages = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberTaskAssignment> taskAssignments = new ArrayList<>();

    public static enum Role {
        OWNER, MANAGER, USER
    }

    @PrePersist
    public void onPrePersist() {
        if (joinDate == null) {
            joinDate = LocalDateTime.now();
        }
    }
}