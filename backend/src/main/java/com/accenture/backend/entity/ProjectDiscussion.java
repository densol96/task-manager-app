package com.accenture.backend.entity;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_discussions")
@Getter
@Setter
@NoArgsConstructor
public class ProjectDiscussion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "posted_by", nullable = false, updatable = false)
    private ProjectMember postedBy;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectDiscussionMessage> messages = new ArrayList<>();

    @Builder
    public ProjectDiscussion(String title, LocalDateTime createdAt, ProjectMember postedBy, Project project) {
        this.title = title;
        this.postedBy = postedBy;
        this.project = project;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}