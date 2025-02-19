package com.accenture.backend.dto.task;

import com.accenture.backend.entity.TaskLabel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskLabelDto {
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title cannot exceed 50 characters")
    private String title;

    @NotBlank(message = "Color is required")
    private String color;

    public static TaskLabelDto fromEntity(TaskLabel label) {
        return new TaskLabelDto(
                label.getTitle(),
                label.getColor()
        );
    }

    public TaskLabel toEntity() {
        TaskLabel label = new TaskLabel();
        label.setTitle(this.title);
        label.setColor(this.color);
        return label;
    }
}
