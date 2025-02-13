package com.accenture.backend.repository;

import com.accenture.backend.entity.MemberTaskAssignment;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTaskAssignmentRepository extends JpaRepository<MemberTaskAssignment, Long> {
    List<MemberTaskAssignment> findByTaskId(Long taskId);
    Optional<MemberTaskAssignment> findByTaskAndMember(Task task, ProjectMember member);
    boolean existsByTaskAndMember(Task task, ProjectMember member);
}