package com.accenture.backend.repository;

import com.accenture.backend.entity.Task;
import com.accenture.backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAll();

    List<Task> findByProjectId(Long projectId);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
}
