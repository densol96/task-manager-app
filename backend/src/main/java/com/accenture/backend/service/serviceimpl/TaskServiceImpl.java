package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.dto.task.TaskDiscussionMessageDto;
import com.accenture.backend.dto.task.TaskDto;
import com.accenture.backend.dto.task.TaskLabelDto;
import com.accenture.backend.dto.task.TaskWithAssigneesDto;
import com.accenture.backend.dto.task.TaskWithLabelsDto;
import com.accenture.backend.entity.MemberTaskAssignment;
import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.Task;
import com.accenture.backend.entity.TaskDiscussionMessage;
import com.accenture.backend.entity.TaskLabel;
import com.accenture.backend.enums.TaskStatus;
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

    private void checkProjectPermissions(Long projectId, boolean canModify, boolean isLabelAction) {
        Long loggedInUserId = userService.getLoggedInUserId();
        ProjectMember member = projectMemberRepository.findByUserIdAndProjectId(loggedInUserId, projectId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this project"));

        if (canModify && !(member.getProjectRole() == ProjectMember.Role.OWNER || member.getProjectRole() == ProjectMember.Role.MANAGER)) {
            //throw new RuntimeException("Permission denied. Only OWNER or MANAGER can modify tasks.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied. Only OWNER or MANAGER can modify tasks.");
        }
        if (!canModify && !isLabelAction && member.getProjectRole() == ProjectMember.Role.USER) {
            //throw new RuntimeException("Permission denied. USERS can only view tasks.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied. USERS can only view tasks.");
        }
    }

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        Project project = projectRepository.findById(taskDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + taskDto.getProjectId()));

        checkProjectPermissions(project.getId(), true, false);

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
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        checkProjectPermissions(existingTask.getProject().getId(), true, false);

        existingTask.setTitle(taskDto.getTitle());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setStatus(taskDto.getStatus());
        existingTask.setPriority(taskDto.getPriority());
        existingTask.setDeadline(taskDto.getDeadline());
        taskRepository.save(existingTask);

        return TaskDto.fromEntity(existingTask);
    }

    @Override
    public TaskDto updateTaskStatus(Long taskId, String status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        checkProjectPermissions(task.getProject().getId(), true, false);

        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        task.setStatus(taskStatus);
        taskRepository.save(task);

        return TaskDto.fromEntity(task);
    }


    @Override
    public List<TaskDto> getTasksByProject(Long projectId) {
        Long loggedInUserId = userService.getLoggedInUserId();
        boolean isProjectMember = projectMemberRepository.existsByUserIdAndProjectId(loggedInUserId, projectId);
        if (!isProjectMember) {
            throw new RuntimeException("Permission denied. User is not a member of this project.");
        }

        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksByProjectAndStatus(Long projectId, String status) {
        Long loggedInUserId = userService.getLoggedInUserId();
        boolean isProjectMember = projectMemberRepository.existsByUserIdAndProjectId(loggedInUserId, projectId);

        if (!isProjectMember) {
            throw new RuntimeException("Permission denied. User is not a member of this project.");
        }

        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        return taskRepository.findByProjectIdAndStatus(projectId, taskStatus).stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        checkProjectPermissions(task.getProject().getId(), true, false);
        taskRepository.deleteById(taskId);
        return true;
    }

    @Override
    public TaskLabelDto createLabel(TaskLabelDto taskLabelDto) {
        TaskLabel label = taskLabelDto.toEntity();
        label = taskLabelRepository.save(label);
        return TaskLabelDto.fromEntity(label);
    }

    @Override
    public TaskLabelDto updateLabel(Long labelId, TaskLabelDto taskLabelDto) {
        return taskLabelRepository.findById(labelId).map(existingLabel -> {
            // Get project ID from the first associated task
            Long projectId = existingLabel.getTasks().stream()
                    .findFirst()
                    .map(task -> task.getProject().getId())
                    .orElseThrow(() -> new RuntimeException("No associated task found for label"));

            checkProjectPermissions(projectId, false, true);

            existingLabel.setTitle(taskLabelDto.getTitle());
            existingLabel.setColor(taskLabelDto.getColor());
            taskLabelRepository.save(existingLabel);
            return TaskLabelDto.fromEntity(existingLabel);
        }).orElse(null);
    }


    @Override
    public boolean deleteFreeLabel(Long labelId) {
        if (taskLabelRepository.existsById(labelId)) {
            taskLabelRepository.deleteById(labelId);
            return true;
        }
        return false;
    }

    @Override
    public TaskWithLabelsDto addLabelToTask(Long taskId, Long labelId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        checkProjectPermissions(task.getProject().getId(), false, true);

        TaskLabel label = taskLabelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        task.getLabels().add(label);
        taskRepository.save(task);
        return TaskWithLabelsDto.fromEntity(task);
    }

    @Override
    public TaskWithLabelsDto removeLabelFromTask(Long taskId, Long labelId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        checkProjectPermissions(task.getProject().getId(), false, true);

        TaskLabel label = taskLabelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        if (!task.getLabels().contains(label)) {
            throw new RuntimeException("Label is not associated with this task.");
        }

        task.getLabels().remove(label);
        taskRepository.save(task);

        if (label.getTasks().isEmpty()) {
            taskLabelRepository.delete(label);
        }

        return TaskWithLabelsDto.fromEntity(task);
    }

    @Override
    public List<TaskLabelDto> getLabelsForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        checkProjectPermissions(task.getProject().getId(), false, true);

        return task.getLabels().stream()
                .map(TaskLabelDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public TaskWithLabelsDto getTaskWithLabels(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        checkProjectPermissions(task.getProject().getId(), false, true);

        return TaskWithLabelsDto.fromEntity(task);
    }

    @Override
    public TaskDiscussionMessageDto addMessageToTask(Long taskId, TaskDiscussionMessageDto messageDto) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        checkProjectPermissions(task.getProject().getId(), false, false);

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

    @Override
    public boolean deleteMessage(Long messageId) {
        TaskDiscussionMessage message = taskDiscussionMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        checkProjectPermissions(message.getTask().getProject().getId(), true, false);

        taskDiscussionMessageRepository.deleteById(messageId);
        return true;
    }

    @Override
    public List<TaskDiscussionMessageDto> getAllMessagesForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        checkProjectPermissions(task.getProject().getId(), false, false);
        return taskDiscussionMessageRepository.findByTaskId(taskId).stream()
                .map(TaskDiscussionMessageDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public TaskWithAssigneesDto assignMemberToTask(Long taskId, Long memberId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        checkProjectPermissions(task.getProject().getId(), true, false);

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

    @Override
    public void unassignMemberFromTask(Long taskId, Long memberId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        checkProjectPermissions(task.getProject().getId(), true, false);

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Project member not found with ID: " + memberId));

        MemberTaskAssignment assignment = memberTaskAssignmentRepository.findByTaskAndMember(task, member)
                .orElseThrow(() -> new RuntimeException("Member is not assigned to this task."));

        memberTaskAssignmentRepository.delete(assignment);
    }

    @Override
    public TaskWithAssigneesDto getAllAssigneesForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        checkProjectPermissions(task.getProject().getId(), false, false);

        return TaskWithAssigneesDto.fromEntity(task);
    }
}