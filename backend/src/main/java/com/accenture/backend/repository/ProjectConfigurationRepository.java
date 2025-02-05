package com.accenture.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accenture.backend.entity.ProjectConfiguration;

public interface ProjectConfigurationRepository extends JpaRepository<ProjectConfiguration, Long> {

}
