package com.accenture.backend.repository;

import com.accenture.backend.entity.TaskLabel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskLabelRepository extends JpaRepository<TaskLabel, Long> {
}
