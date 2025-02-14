package com.accenture.backend.controller;

import com.accenture.backend.dto.task.TaskDiscussionMessageDto;
import com.accenture.backend.dto.task.TaskDto;
import com.accenture.backend.dto.task.TaskLabelDto;
import com.accenture.backend.dto.task.TaskWithAssigneesDto;
import com.accenture.backend.dto.task.TaskWithLabelsDto;
import com.accenture.backend.service.serviceimpl.TaskServiceImpl;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskServiceImpl taskService;

    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/update/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(taskId, taskDto);
        return updatedTask != null ? ResponseEntity.ok(updatedTask) : ResponseEntity.notFound().build();
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<TaskDto>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskDto> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable("id") Long taskId,
            @RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");

        TaskDto updatedTask = taskService.updateTaskStatus(taskId, status);

        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/projects/{projectId}/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByProjectAndStatus(@PathVariable Long projectId,
            @PathVariable String status) {
        List<TaskDto> tasks = taskService.getTasksByProjectAndStatus(projectId, status);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        boolean isDeleted = taskService.deleteTask(taskId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Not bound to any task or project, no permission needed
    @PostMapping("/labels/add")
    public ResponseEntity<TaskLabelDto> createLabel(@RequestBody TaskLabelDto taskLabelDto) {
        TaskLabelDto createdLabel = taskService.createLabel(taskLabelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabel);
    }

    // Updates a label, that was added to some task (by addLabelToTask)
    @PutMapping("/labels/{labelId}")
    public ResponseEntity<TaskLabelDto> updateLabel(@PathVariable Long labelId,
            @RequestBody TaskLabelDto taskLabelDto) {
        TaskLabelDto updatedLabel = taskService.updateLabel(labelId, taskLabelDto);
        return updatedLabel != null ? ResponseEntity.ok(updatedLabel) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/labels/delete/{labelId}")
    public ResponseEntity<Void> deleteFreeLabel(@PathVariable Long labelId) {
        boolean isDeleted = taskService.deleteFreeLabel(labelId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{taskId}/labels/{labelId}/addToTask")
    public ResponseEntity<TaskWithLabelsDto> addLabelToTask(@PathVariable Long taskId, @PathVariable Long labelId) {
        TaskWithLabelsDto updatedTask = taskService.addLabelToTask(taskId, labelId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}/labels/deleteFromTask/{labelId}")
    public ResponseEntity<TaskWithLabelsDto> removeLabelFromTask(@PathVariable Long taskId,
            @PathVariable Long labelId) {
        TaskWithLabelsDto updatedTask = taskService.removeLabelFromTask(taskId, labelId);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{taskId}/labels")
    public ResponseEntity<List<TaskLabelDto>> getLabelsForTask(@PathVariable Long taskId) {
        List<TaskLabelDto> labels = taskService.getLabelsForTask(taskId);
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{taskId}/taskWithLabels")
    public ResponseEntity<TaskWithLabelsDto> getTaskWithLabels(@PathVariable Long taskId) {
        TaskWithLabelsDto taskWithLabels = taskService.getTaskWithLabels(taskId);
        if (taskWithLabels == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskWithLabels);
    }

    @PostMapping("/{taskId}/messages/add")
    public ResponseEntity<TaskDiscussionMessageDto> addMessageToTask(@PathVariable Long taskId,
            @RequestBody TaskDiscussionMessageDto messageDto) {
        TaskDiscussionMessageDto createdMessage = taskService.addMessageToTask(taskId, messageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @DeleteMapping("/messages/delete/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        boolean isDeleted = taskService.deleteMessage(messageId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{taskId}/messages")
    public ResponseEntity<List<TaskDiscussionMessageDto>> getAllMessagesForTask(@PathVariable Long taskId) {
        List<TaskDiscussionMessageDto> messages = taskService.getAllMessagesForTask(taskId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{taskId}/assign/{memberId}")
    public ResponseEntity<TaskWithAssigneesDto> assignMemberToTask(@PathVariable Long taskId,
            @PathVariable Long memberId) {
        TaskWithAssigneesDto updatedTask = taskService.assignMemberToTask(taskId, memberId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}/unassign/{memberId}")
    public ResponseEntity<Void> unassignMemberFromTask(@PathVariable Long taskId, @PathVariable Long memberId) {
        taskService.unassignMemberFromTask(taskId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/assignees")
    public ResponseEntity<TaskWithAssigneesDto> getAllAssigneesForTask(@PathVariable Long taskId) {
        TaskWithAssigneesDto taskWithAssignees = taskService.getAllAssigneesForTask(taskId);
        return ResponseEntity.ok(taskWithAssignees);
    }
}