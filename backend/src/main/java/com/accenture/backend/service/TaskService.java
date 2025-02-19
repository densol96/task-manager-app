package com.accenture.backend.service;

import com.accenture.backend.dto.task.*;
import com.accenture.backend.entity.Task;
import com.accenture.backend.entity.TaskLabel;

import java.util.List;

public interface TaskService {
    TaskDto createTask(TaskDto taskDto);
    TaskDto updateTask(Long taskId, TaskDto taskDto);
    TaskDto updateTaskStatus(Long taskId, TaskStatusUpdateDto statusDto);

    List<TaskDto> getTasksByProject(Long projectId);
    List<TaskDto> getTasksByProjectAndStatus(Long projectId, String status);
    boolean deleteTask(Long taskId);

    List<TaskLabelDto> getLabelsForTask(Long taskId);
    TaskLabelDto createLabel(Long projectId, TaskLabelDto taskLabelDto);
    TaskLabelDto updateLabel(Long projectId, Long labelId, TaskLabelDto taskLabelDto);
    boolean deleteLabel(Long projectId, Long labelId);

    TaskWithLabelsDto addLabelToTask(Long taskId, Long labelId);
    String removeLabelFromTask(Long taskId, Long labelId);
    TaskWithLabelsDto getTaskWithLabels(Long taskId);

    TaskDiscussionMessageDto addMessageToTask(Long taskId, TaskDiscussionMessageDto messageDto);
    boolean deleteMessage(Long messageId);
    List<TaskDiscussionMessageDto> getAllMessagesForTask(Long taskId);

    TaskWithAssigneesDto assignMemberToTask(Long taskId, Long memberId);
    void unassignMemberFromTask(Long taskId, Long memberId);
    List<MemberTaskAssignmentDto> getAllAssigneesForTask(Long taskId);
    Task findTaskById(Long taskId);
    TaskLabel findLabelById(Long labelId);
    void findProjectById(Long projectId);
}

