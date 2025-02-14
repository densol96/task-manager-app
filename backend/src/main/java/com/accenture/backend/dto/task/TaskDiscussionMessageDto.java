package com.accenture.backend.dto.task;

import com.accenture.backend.dto.response.UserShortDto;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.TaskDiscussionMessage;
import com.accenture.backend.entity.User;
import com.accenture.backend.exception.EntityNotFoundException;

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
    private Long authorId;
    private UserShortDto userInfo;
    private String message;
    private LocalDateTime postedAt;

    public static TaskDiscussionMessageDto fromEntity(TaskDiscussionMessage message) {
        ProjectMember author = message.getAuthor();
        User user = author.getUser();
        if (user == null || author == null)
            throw new EntityNotFoundException("Associated entity is not found!");

        UserShortDto userShortInfo = UserShortDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail()).build();
        return new TaskDiscussionMessageDto(
                message.getId(),
                message.getTask().getId(),
                author.getId(),
                userShortInfo,
                message.getMessage(),
                message.getPostedAt());
    }

    public TaskDiscussionMessage toEntity() {
        TaskDiscussionMessage message = new TaskDiscussionMessage();
        message.setId(this.id);
        message.setMessage(this.message);
        message.setPostedAt(this.postedAt != null ? this.postedAt : LocalDateTime.now());
        return message;
    }
}