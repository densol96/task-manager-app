package com.accenture.backend.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_interactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private LocalDateTime responseDate;

    public static enum Type {
        INVITATION, APPLICATION
    }

    public static enum Status {
        PENDING, ACCEPTED, DECLINED
    }

    @PrePersist
    public void onPrePersist() {
        if (initAt == null) {
            initAt = LocalDateTime.now();
        }
    }

}