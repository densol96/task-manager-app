package com.accenture.backend.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accenture.backend.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByConfigIsPublicTrue();
}