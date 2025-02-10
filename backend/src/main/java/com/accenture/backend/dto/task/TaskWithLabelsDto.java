package com.accenture.backend.dto.task;

import com.accenture.backend.entity.Task;
import com.accenture.backend.enums.TaskPriority;
import com.accenture.backend.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithLabelsDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private List<TaskLabelDto> labels;

    public static TaskWithLabelsDto fromEntity(Task task) {
        return new TaskWithLabelsDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getLabels().stream().map(TaskLabelDto::fromEntity).collect(Collectors.toList())
        );
    }
}
