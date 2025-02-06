package com.accenture.backend.repository;

import com.accenture.backend.model.ProjectDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectDiscussionRepository extends JpaRepository<ProjectDiscussion, Integer> {
}