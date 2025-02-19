package com.accenture.backend.controller;

import com.accenture.backend.dto.task.MemberTaskAssignmentDto;
import com.accenture.backend.dto.task.TaskDiscussionMessageDto;
import com.accenture.backend.dto.task.TaskDto;
import com.accenture.backend.dto.task.TaskLabelDto;
import com.accenture.backend.dto.task.TaskStatusUpdateDto;
import com.accenture.backend.dto.task.TaskWithAssigneesDto;
import com.accenture.backend.dto.task.TaskWithLabelsDto;
import com.accenture.backend.service.serviceimpl.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task Controller")
public class TaskController {
    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Adds a new task for a project")
    @PostMapping("/add")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Operation(summary = "Updates a task")
    @PutMapping("/update/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(taskId, taskDto);
        return updatedTask != null ? ResponseEntity.ok(updatedTask) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Updates a task status")
    @PutMapping("/update/{taskId}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long taskId, @RequestBody @Valid TaskStatusUpdateDto statusDto) {

        TaskDto updatedTask = taskService.updateTaskStatus(taskId, statusDto);
        return updatedTask != null ? ResponseEntity.ok(updatedTask) : ResponseEntity.notFound().build();
    }


    @Operation(summary = "Retrieves tasks by project Id")
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<TaskDto>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskDto> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Retrieves tasks by project Id and task status")
    @GetMapping("/projects/{projectId}/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByProjectAndStatus(@PathVariable Long projectId, @PathVariable String status) {
        List<TaskDto> tasks = taskService.getTasksByProjectAndStatus(projectId, status);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Deletes a task")
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        boolean isDeleted = taskService.deleteTask(taskId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Creates a label that isn't linked to any task yet")
    @PostMapping("/projects/{projectId}/labels/add")
    public ResponseEntity<TaskLabelDto> createLabel(@PathVariable Long projectId, @RequestBody TaskLabelDto taskLabelDto) {
        TaskLabelDto createdLabel = taskService.createLabel(projectId, taskLabelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabel);
    }

    @Operation(summary = "Updates a label")
    @PutMapping("/projects/{projectId}/labels/{labelId}")
    public ResponseEntity<TaskLabelDto> updateLabel(@PathVariable Long projectId, @PathVariable Long labelId, @RequestBody TaskLabelDto taskLabelDto) {
        TaskLabelDto updatedLabel = taskService.updateLabel(projectId, labelId, taskLabelDto);
        return updatedLabel != null ? ResponseEntity.ok(updatedLabel) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Deletes a label")
    @DeleteMapping("/projects/{projectId}/labels/delete/{labelId}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long projectId, @PathVariable Long labelId) {
        boolean isDeleted = taskService.deleteLabel(projectId, labelId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Adds a label to a task")
    @PostMapping("/{taskId}/labels/{labelId}/addToTask")
    public ResponseEntity<TaskWithLabelsDto> addLabelToTask(@PathVariable Long taskId, @PathVariable Long labelId) {
        TaskWithLabelsDto updatedTask = taskService.addLabelToTask(taskId, labelId);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Removes a label from a task")
    @DeleteMapping("/{taskId}/labels/deleteFromTask/{labelId}")
    public ResponseEntity<String> removeLabelFromTask(@PathVariable Long taskId, @PathVariable Long labelId) {
        String updatedTask = taskService.removeLabelFromTask(taskId, labelId);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Retrieves all labels linked to a task")
    @GetMapping("/{taskId}/labels")
    public ResponseEntity<List<TaskLabelDto>> getLabelsForTask(@PathVariable Long taskId) {
        List<TaskLabelDto> labels = taskService.getLabelsForTask(taskId);
        return ResponseEntity.ok(labels);
    }

    @Operation(summary = "Retrieves a task by Id with all linked labels")
    @GetMapping("/{taskId}/taskWithLabels")
    public ResponseEntity<TaskWithLabelsDto> getTaskWithLabels(@PathVariable Long taskId) {
        TaskWithLabelsDto taskWithLabels = taskService.getTaskWithLabels(taskId);
        if (taskWithLabels == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskWithLabels);
    }

    @Operation(summary = "Creates a message linked to a task")
    @PostMapping("/{taskId}/messages/add")
    public ResponseEntity<TaskDiscussionMessageDto> addMessageToTask(@PathVariable Long taskId, @RequestBody TaskDiscussionMessageDto messageDto) {
        TaskDiscussionMessageDto createdMessage = taskService.addMessageToTask(taskId, messageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @Operation(summary = "Deletes a message linked to a task")
    @DeleteMapping("/messages/delete/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        boolean isDeleted = taskService.deleteMessage(messageId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Retrieves all messages linked to a task")
    @GetMapping("/{taskId}/messages")
    public ResponseEntity<List<TaskDiscussionMessageDto>> getAllMessagesForTask(@PathVariable Long taskId) {
        List<TaskDiscussionMessageDto> messages = taskService.getAllMessagesForTask(taskId);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Assigns a member to a task")
    @PostMapping("/{taskId}/assign/{memberId}")
    public ResponseEntity<TaskWithAssigneesDto> assignMemberToTask(@PathVariable Long taskId, @PathVariable Long memberId) {
        TaskWithAssigneesDto updatedTask = taskService.assignMemberToTask(taskId, memberId);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Unassigns a member from a task")
    @DeleteMapping("/{taskId}/unassign/{memberId}")
    public ResponseEntity<Void> unassignMemberFromTask(@PathVariable Long taskId, @PathVariable Long memberId) {
        taskService.unassignMemberFromTask(taskId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retrieves all assignees for a task")
    @GetMapping("/{taskId}/assignees")
    public ResponseEntity<List<MemberTaskAssignmentDto>> getAllAssigneesForTask(@PathVariable Long taskId) {
        List<MemberTaskAssignmentDto> assignees = taskService.getAllAssigneesForTask(taskId);
        return ResponseEntity.ok(assignees);
    }
}