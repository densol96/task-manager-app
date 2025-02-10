package com.accenture.backend.service;

import com.accenture.backend.dto.task.*;
import com.accenture.backend.entity.*;
import com.accenture.backend.enums.TaskStatus;
import com.accenture.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskLabelRepository taskLabelRepository;
    private final TaskDiscussionMessageRepository taskDiscussionMessageRepository;
    private final MemberTaskAssignmentRepository memberTaskAssignmentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public TaskService(TaskRepository taskRepository, TaskLabelRepository taskLabelRepository,
                       TaskDiscussionMessageRepository taskDiscussionMessageRepository,
                       MemberTaskAssignmentRepository memberTaskAssignmentRepository, ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository) {
        this.taskRepository = taskRepository;
        this.taskLabelRepository = taskLabelRepository;
        this.taskDiscussionMessageRepository = taskDiscussionMessageRepository;
        this.memberTaskAssignmentRepository = memberTaskAssignmentRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream().map(TaskDto::fromEntity).collect(Collectors.toList());
    }

    public TaskDto createTask(TaskDto taskDto) {
        Project project = projectRepository.findById(taskDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + taskDto.getProjectId()));

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        task.setDeadline(taskDto.getDeadline());
        task.setProject(project);

        Task savedTask = taskRepository.save(task);
        return TaskDto.fromEntity(savedTask);
    }

    public TaskDto updateTask(Long taskId, TaskDto taskDto) {
        return taskRepository.findById(taskId).map(existingTask -> {
            existingTask.setTitle(taskDto.getTitle());
            existingTask.setDescription(taskDto.getDescription());
            existingTask.setStatus(taskDto.getStatus());
            existingTask.setPriority(taskDto.getPriority());
            existingTask.setDeadline(taskDto.getDeadline());
            taskRepository.save(existingTask);
            return TaskDto.fromEntity(existingTask);
        }).orElse(null);
    }

    @Transactional
    public TaskDto updateTaskStatus(Long taskId, String status) {
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());

            return taskRepository.findById(taskId).map(task -> {
                task.setStatus(taskStatus);
                taskRepository.save(task);
                return TaskDto.fromEntity(task);
            }).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public List<TaskDto> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream().map(TaskDto::fromEntity).collect(Collectors.toList());
    }

    public List<TaskDto> getTasksByStatus(String status) {
        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        return taskRepository.findByStatus(taskStatus).stream().map(TaskDto::fromEntity).collect(Collectors.toList());
    }

    public boolean deleteTask(Long taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }

    public List<TaskLabelDto> getAllLabels() {
        return taskLabelRepository.findAll().stream().map(TaskLabelDto::fromEntity).collect(Collectors.toList());
    }

    public TaskLabelDto createLabel(TaskLabelDto taskLabelDto) {
        TaskLabel label = taskLabelDto.toEntity();
        label = taskLabelRepository.save(label);
        return TaskLabelDto.fromEntity(label);
    }

    public TaskLabelDto updateLabel(Long labelId, TaskLabelDto taskLabelDto) {
        return taskLabelRepository.findById(labelId).map(existingLabel -> {
            existingLabel.setTitle(taskLabelDto.getTitle());
            existingLabel.setColor(taskLabelDto.getColor());
            taskLabelRepository.save(existingLabel);
            return TaskLabelDto.fromEntity(existingLabel);
        }).orElse(null);
    }

    public boolean deleteLabel(Long labelId) {
        if (taskLabelRepository.existsById(labelId)) {
            taskLabelRepository.deleteById(labelId);
            return true;
        }
        return false;
    }

    @Transactional
    public TaskWithLabelsDto addLabelToTask(Long taskId, Long labelId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        TaskLabel label = taskLabelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + labelId));

        task.getLabels().add(label);
        taskRepository.save(task);

        return TaskWithLabelsDto.fromEntity(task);
    }

    public TaskWithLabelsDto getTaskWithLabels(Long taskId) {
        Task task = taskRepository.findTaskWithLabels(taskId);
        return task != null ? TaskWithLabelsDto.fromEntity(task) : null;
    }

    @Transactional
    public TaskDiscussionMessageDto addMessageToTask(Long taskId, TaskDiscussionMessageDto messageDto) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }

        ProjectMember author = projectMemberRepository.findById(messageDto.getAuthorId()).orElse(null);
        if (author == null) {
            throw new RuntimeException("Author not found");
        }

        TaskDiscussionMessage message = messageDto.toEntity();
        message.setTask(task);
        message.setAuthor(author);

        message = taskDiscussionMessageRepository.save(message);
        return TaskDiscussionMessageDto.fromEntity(message);
    }

    public boolean deleteMessage(Long messageId) {
        if (taskDiscussionMessageRepository.existsById(messageId)) {
            taskDiscussionMessageRepository.deleteById(messageId);
            return true;
        }
        return false;
    }

    public List<TaskDiscussionMessageDto> getAllMessagesForTask(Long taskId) {
        return taskDiscussionMessageRepository.findByTaskId(taskId).stream()
                .map(TaskDiscussionMessageDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public TaskWithAssigneesDto assignMemberToTask(Long taskId, Long memberId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Project member not found with ID: " + memberId));

        boolean alreadyAssigned = memberTaskAssignmentRepository.existsByTaskAndMember(task, member);
        if (alreadyAssigned) {
            throw new RuntimeException("Member is already assigned to this task.");
        }

        MemberTaskAssignment assignment = new MemberTaskAssignment();
        assignment.setTask(task);
        assignment.setMember(member);
        assignment.setAssignedOn(LocalDateTime.now());

        memberTaskAssignmentRepository.save(assignment);
        return TaskWithAssigneesDto.fromEntity(task);
    }

    public void unassignMemberFromTask(Long taskId, Long memberId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Project member not found with ID: " + memberId));

        MemberTaskAssignment assignment = memberTaskAssignmentRepository.findByTaskAndMember(task, member)
                .orElseThrow(() -> new RuntimeException("Member is not assigned to this task."));

        memberTaskAssignmentRepository.delete(assignment);
    }

    @Transactional(readOnly = true)
    public TaskWithAssigneesDto getAllAssigneesForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        return TaskWithAssigneesDto.fromEntity(task);
    }
}