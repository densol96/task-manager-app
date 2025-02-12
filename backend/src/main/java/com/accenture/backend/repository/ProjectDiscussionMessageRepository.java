package com.accenture.backend.repository;

import com.accenture.backend.entity.ProjectDiscussionMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectDiscussionMessageRepository extends JpaRepository<ProjectDiscussionMessage, Integer> {
    List<ProjectDiscussionMessage> findByDiscussionId(Integer discussionId);
}

