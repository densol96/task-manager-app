package com.accenture.backend.repository;

import com.accenture.backend.entity.Task;
import com.accenture.backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAll();

    List<Task> findByProjectId(Long projectId);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.labels WHERE t.id = :taskId")
    Task findTaskWithLabels(@Param("taskId") Long taskId);

    List<Task> findByStatus(TaskStatus status);
}
