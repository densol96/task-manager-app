package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.dto.task.MemberTaskAssignmentDto;
import com.accenture.backend.dto.task.TaskDiscussionMessageDto;
import com.accenture.backend.dto.task.TaskDto;
import com.accenture.backend.dto.task.TaskLabelDto;
import com.accenture.backend.dto.task.TaskStatusUpdateDto;
import com.accenture.backend.dto.task.TaskWithAssigneesDto;
import com.accenture.backend.dto.task.TaskWithLabelsDto;
import com.accenture.backend.entity.MemberTaskAssignment;
import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.Task;
import com.accenture.backend.entity.TaskDiscussionMessage;
import com.accenture.backend.entity.TaskLabel;
import com.accenture.backend.enums.TaskStatus;
import com.accenture.backend.exception.ResponseStatusExceptionIfProjectMember;
import com.accenture.backend.exception.ResponseStatusExceptionIfProjectMemberManagerOrOwner;
import com.accenture.backend.repository.MemberTaskAssignmentRepository;
import com.accenture.backend.repository.ProjectMemberRepository;
import com.accenture.backend.repository.ProjectRepository;
import com.accenture.backend.repository.TaskDiscussionMessageRepository;
import com.accenture.backend.repository.TaskLabelRepository;
import com.accenture.backend.repository.TaskRepository;
import com.accenture.backend.service.TaskService;
import com.accenture.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskLabelRepository taskLabelRepository;
    private final TaskDiscussionMessageRepository taskDiscussionMessageRepository;
    private final MemberTaskAssignmentRepository memberTaskAssignmentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserService userService;

    private void checkProjectPermissions(Long projectId, boolean requiresManagerOrOwner) {
        Long loggedInUserId = userService.getLoggedInUserId();

        ProjectMember member = projectMemberRepository.findByUserIdAndProjectId(loggedInUserId, projectId)
                .orElseThrow(ResponseStatusExceptionIfProjectMember::new);

        if (requiresManagerOrOwner && !(member.getProjectRole() == ProjectMember.Role.OWNER ||
                member.getProjectRole() == ProjectMember.Role.MANAGER)) {
            throw new ResponseStatusExceptionIfProjectMemberManagerOrOwner();
        }
    }

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        Project project = projectRepository.findById(taskDto.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with ID: " + taskDto.getProjectId()));

        checkProjectPermissions(project.getId(), true);

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
        Task existingTask = findTaskById(taskId);

        checkProjectPermissions(existingTask.getProject().getId(), true);

        existingTask.setTitle(taskDto.getTitle());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setStatus(taskDto.getStatus());
        existingTask.setPriority(taskDto.getPriority());
        existingTask.setDeadline(taskDto.getDeadline());
        taskRepository.save(existingTask);

        return TaskDto.fromEntity(existingTask);
    }

    @Override
    public TaskDto updateTaskStatus(Long taskId, TaskStatusUpdateDto statusDto) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), true);

        task.setStatus(statusDto.getStatus());
        taskRepository.save(task);

        return TaskDto.fromEntity(task);
    }

    @Override
    public List<TaskDto> getTasksByProject(Long projectId) {
        findProjectById(projectId);

        checkProjectPermissions(projectId, false);

        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksByProjectAndStatus(Long projectId, String status) {
        findProjectById(projectId);
        checkProjectPermissions(projectId, false);

        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        return taskRepository.findByProjectIdAndStatus(projectId, taskStatus).stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteTask(Long taskId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), true);

        taskRepository.deleteById(taskId);
        return true;
    }

    @Override
    public TaskLabelDto createLabel(Long projectId, TaskLabelDto taskLabelDto) {
        findProjectById(projectId);
        checkProjectPermissions(projectId, false);

        TaskLabel label = taskLabelDto.toEntity();
        label = taskLabelRepository.save(label);
        return TaskLabelDto.fromEntity(label);
    }

    @Override
    public TaskLabelDto updateLabel(Long projectId, Long labelId, TaskLabelDto taskLabelDto) {
        findProjectById(projectId);

        checkProjectPermissions(projectId, false);

        TaskLabel label = findLabelById(labelId);

        if (!label.getTasks().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update label. It is assigned to one or more tasks.");
        }

        label.setTitle(taskLabelDto.getTitle());
        label.setColor(taskLabelDto.getColor());

        taskLabelRepository.save(label);

        return TaskLabelDto.fromEntity(label);
    }

    @Override
    public boolean deleteLabel(Long projectId, Long labelId) {
        checkProjectPermissions(projectId, false);

        TaskLabel label = findLabelById(labelId);

        if (!label.getTasks().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete label. It is assigned to one or more tasks.");
        }

        taskLabelRepository.deleteById(labelId);
        return true;
    }

    @Override
    public TaskWithLabelsDto addLabelToTask(Long taskId, Long labelId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), false);

        TaskLabel label = findLabelById(labelId);

        task.getLabels().add(label);
        taskRepository.save(task);
        return TaskWithLabelsDto.fromEntity(task);
    }

    @Override
    public String removeLabelFromTask(Long taskId, Long labelId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), false);

        TaskLabel label = findLabelById(labelId);

        if (!task.getLabels().contains(label)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Label is not associated with this task.");
        }

        task.getLabels().remove(label);
        taskRepository.save(task);

        if (label.getTasks().isEmpty()) {
            taskLabelRepository.delete(label);
        }

        return "Label with ID " + labelId + " was successfully removed from Task with ID " + taskId;
    }

    @Override
    public List<TaskLabelDto> getLabelsForTask(Long taskId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), false);

        return task.getLabels().stream()
                .map(TaskLabelDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public TaskWithLabelsDto getTaskWithLabels(Long taskId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), false);

        return TaskWithLabelsDto.fromEntity(task);
    }

    @Override
    public TaskDiscussionMessageDto addMessageToTask(Long taskId, TaskDiscussionMessageDto messageDto) {

        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), false);

        ProjectMember author = projectMemberRepository.findById(messageDto.getAuthorId()).orElse(null);
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
        }

        TaskDiscussionMessage message = messageDto.toEntity();
        message.setTask(task);
        message.setAuthor(author);

        message = taskDiscussionMessageRepository.save(message);
        return TaskDiscussionMessageDto.fromEntity(message);
    }

    @Override
    public boolean deleteMessage(Long messageId) {
        TaskDiscussionMessage message = taskDiscussionMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        checkProjectPermissions(message.getTask().getProject().getId(), false);

        taskDiscussionMessageRepository.deleteById(messageId);
        return true;
    }

    @Override
    public List<TaskDiscussionMessageDto> getAllMessagesForTask(Long taskId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), false);

        return taskDiscussionMessageRepository.findByTaskId(taskId).stream()
                .map(TaskDiscussionMessageDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public TaskWithAssigneesDto assignMemberToTask(Long taskId, Long memberId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), true);

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project member not found with ID: " + memberId));

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

    @Override
    public void unassignMemberFromTask(Long taskId, Long memberId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), true);

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project member not found with ID: " + memberId));

        MemberTaskAssignment assignment = memberTaskAssignmentRepository.findByTaskAndMember(task, member)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member is not assigned to this task."));

        memberTaskAssignmentRepository.delete(assignment);
    }

    @Override
    public List<MemberTaskAssignmentDto> getAllAssigneesForTask(Long taskId) {
        Task task = findTaskById(taskId);

        checkProjectPermissions(task.getProject().getId(), false);

        return task.getMemberAssignments().stream()
                .map(MemberTaskAssignmentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId));
    }

    @Override
    public TaskLabel findLabelById(Long labelId) {
        return taskLabelRepository.findById(labelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found with ID: " + labelId));
    }

    @Override
    public void findProjectById(Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with ID: " + projectId));
    }
}