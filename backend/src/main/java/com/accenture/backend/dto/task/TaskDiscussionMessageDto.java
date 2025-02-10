package com.accenture.backend.dto.task;

import com.accenture.backend.entity.TaskDiscussionMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDiscussionMessageDto {
    private Long id;
    private Long taskId;
    private Long authorId; // actual entity is fetched in service layer
    private String message;
    private LocalDateTime postedAt;

    public static TaskDiscussionMessageDto fromEntity(TaskDiscussionMessage message) {
        return new TaskDiscussionMessageDto(
                message.getId(),
                message.getTask().getId(),
                message.getAuthor().getId(),
                message.getMessage(),
                message.getPostedAt()
        );
    }

    public TaskDiscussionMessage toEntity() {
        TaskDiscussionMessage message = new TaskDiscussionMessage();
        message.setId(this.id);
        message.setMessage(this.message);
        message.setPostedAt(this.postedAt != null ? this.postedAt : LocalDateTime.now());
        return message;
    }
}