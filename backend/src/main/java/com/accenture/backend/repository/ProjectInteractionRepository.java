package com.accenture.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accenture.backend.entity.ProjectInteraction;

public interface ProjectInteractionRepository extends JpaRepository<ProjectInteraction, Long> {
    List<ProjectInteraction> findAllByUserIdAndTypeAndStatus(Long userId, ProjectInteraction.Type type,
            ProjectInteraction.Status status);

    List<ProjectInteraction> findAllByProjectIdAndTypeAndStatus(Long projectId, ProjectInteraction.Type type,
            ProjectInteraction.Status status);
}