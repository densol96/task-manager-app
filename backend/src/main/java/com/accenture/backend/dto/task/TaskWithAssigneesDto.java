package com.accenture.backend.dto.task;

import com.accenture.backend.entity.Task;
import com.accenture.backend.enums.TaskPriority;
import com.accenture.backend.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithAssigneesDto extends TaskDto {
    private Set<MemberTaskAssignmentDto> assignees;

    public TaskWithAssigneesDto(Long id, String title, String description, TaskStatus status, TaskPriority priority, LocalDateTime deadline, Long projectId, Set<MemberTaskAssignmentDto> assignees) {
        super(id, title, description, status, priority, deadline, projectId);
        this.assignees = assignees;
    }

    public static TaskWithAssigneesDto fromEntity(Task task) {
        return new TaskWithAssigneesDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDeadline(),
                task.getProject().getId(),
                task.getMemberAssignments().stream()
                        .map(MemberTaskAssignmentDto::fromEntity)
                        .collect(Collectors.toSet())
        );
    }
}