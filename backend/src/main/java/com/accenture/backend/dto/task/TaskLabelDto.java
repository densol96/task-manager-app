package com.accenture.backend.dto.task;

import com.accenture.backend.entity.TaskLabel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskLabelDto {
    private String title;
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
