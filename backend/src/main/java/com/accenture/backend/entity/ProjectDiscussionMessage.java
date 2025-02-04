package com.accenture.backend.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_discussion_messages")
@Getter
@Setter
@NoArgsConstructor
public class ProjectDiscussionMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "posted_by", nullable = false)
    private ProjectMember postedBy;

    @ManyToOne
    @JoinColumn(name = "discussion_id", nullable = false)
    private ProjectDiscussion discussion;

    @Builder
    public ProjectDiscussionMessage(String message, LocalDateTime createdAt,
            ProjectMember postedBy,
            ProjectDiscussion discussion) {
        this.message = message;
        this.postedBy = postedBy;
        this.discussion = discussion;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
