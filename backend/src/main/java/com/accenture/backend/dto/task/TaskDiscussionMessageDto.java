package com.accenture.backend.dto.task;

import com.accenture.backend.entity.TaskDiscussionMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDiscussionMessageDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotBlank(message = "Message content is required")
    private String message;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
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