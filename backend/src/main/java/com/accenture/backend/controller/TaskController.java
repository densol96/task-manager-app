package com.accenture.backend.controller;

import com.accenture.backend.dto.task.*;
import com.accenture.backend.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<TaskDto>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskDto> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/add")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{taskId}/update")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(taskId, taskDto);
        return updatedTask != null ? ResponseEntity.ok(updatedTask) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/update-status")
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

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable String status) {
        List<TaskDto> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        boolean isDeleted = taskService.deleteTask(taskId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{taskId}/labels/{labelId}/add")
    public ResponseEntity<TaskWithLabelsDto> addLabelToTask(@PathVariable Long taskId, @PathVariable Long labelId) {
        TaskWithLabelsDto updatedTask = taskService.addLabelToTask(taskId, labelId);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{taskId}/labels")
    public ResponseEntity<TaskWithLabelsDto> getTaskWithLabels(@PathVariable Long taskId) {
        TaskWithLabelsDto taskWithLabels = taskService.getTaskWithLabels(taskId);
        if (taskWithLabels == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskWithLabels);
    }

    @GetMapping("/labels")
    public ResponseEntity<List<TaskLabelDto>> getAllLabels() {
        List<TaskLabelDto> labels = taskService.getAllLabels();
        return ResponseEntity.ok(labels);
    }

    @PostMapping("/labels/add")
    public ResponseEntity<TaskLabelDto> createLabel(@RequestBody TaskLabelDto taskLabelDto) {
        TaskLabelDto createdLabel = taskService.createLabel(taskLabelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabel);
    }

    @PutMapping("/labels/{labelId}")
    public ResponseEntity<TaskLabelDto> updateLabel(@PathVariable Long labelId, @RequestBody TaskLabelDto taskLabelDto) {
        TaskLabelDto updatedLabel = taskService.updateLabel(labelId, taskLabelDto);
        return updatedLabel != null ? ResponseEntity.ok(updatedLabel) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/labels/delete/{labelId}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long labelId) {
        boolean isDeleted = taskService.deleteLabel(labelId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{taskId}/messages/add")
    public ResponseEntity<TaskDiscussionMessageDto> addMessageToTask(@PathVariable Long taskId, @RequestBody TaskDiscussionMessageDto messageDto) {
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
    public ResponseEntity<TaskWithAssigneesDto> assignMemberToTask(@PathVariable Long taskId, @PathVariable Long memberId) {
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