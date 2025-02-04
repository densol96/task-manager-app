package com.accenture.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_configurations")
@Getter
@Setter
@NoArgsConstructor
public class ProjectConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Integer maxParticipants;

    private String backgroundImage;

    @Builder
    public ProjectConfiguration(Project project, Boolean isPublic, Integer maxParticipants, String backgroundImage) {
        this.project = project;
        this.isPublic = isPublic;
        this.maxParticipants = maxParticipants;
        this.backgroundImage = backgroundImage;
    }

    @PrePersist
    public void prePersist() {
        if (isPublic == null) {
            isPublic = true;
        }
        if (maxParticipants == null) {
            maxParticipants = 10;
        }
    }
}