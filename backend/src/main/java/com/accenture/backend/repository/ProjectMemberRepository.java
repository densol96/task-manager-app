package com.accenture.backend.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accenture.backend.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectIdAndProjectRole(Long projectId, ProjectMember.ProjectRole role);

    Long countAllByUserId(Long userId);
}
