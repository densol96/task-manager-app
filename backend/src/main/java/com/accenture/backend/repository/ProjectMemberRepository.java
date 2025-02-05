package com.accenture.backend.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accenture.backend.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    Long countAllByUserId(Long userId);

    Long countAllByUserIdAndProjectRole(Long userId, ProjectMember.Role role);

    List<ProjectMember> findByProjectIdAndProjectRole(Long projectId, ProjectMember.Role role);

    Optional<ProjectMember> findByUserIdAndProjectId(Long userId, Long projectId);
}
