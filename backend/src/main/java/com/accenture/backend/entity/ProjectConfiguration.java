package com.accenture.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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