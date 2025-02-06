package com.accenture.backend.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_interactions")
@Getter
@Setter
@NoArgsConstructor
public class ProjectInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime initAt;

    @Column
    private LocalDateTime responseDate;

    private String initComment;

    public static enum Type {
        INVITATION, APPLICATION
    }

    public static enum Status {
        PENDING, ACCEPTED, DECLINED
    }

    @Builder
    public ProjectInteraction(User user, Project project, Type type, Status status,
            LocalDateTime initAt, LocalDateTime responseDate, String initComment) {
        this.user = user;
        this.project = project;
        this.type = type;
        this.status = status;
        this.initAt = initAt;
        this.responseDate = responseDate;
        this.initComment = initComment;
    }

    @PrePersist
    public void onPrePersist() {
        if (initAt == null) {
            initAt = LocalDateTime.now();
        }
    }

}