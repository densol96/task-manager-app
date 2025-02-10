package com.accenture.backend.service.serviceimpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.accenture.backend.dto.task.*;
import com.accenture.backend.entity.*;
import com.accenture.backend.enums.TaskPriority;
import com.accenture.backend.enums.TaskStatus;
import com.accenture.backend.repository.*;
import com.accenture.backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskLabelRepository taskLabelRepository;

    @Mock
    private MemberTaskAssignmentRepository memberTaskAssignmentRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    private Task task;
    private Project project;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        project = new Project("Test Project", "Description", LocalDateTime.now());
        setEntityId(project, 1L);

        task = new Task();
        setEntityId(task, 1L);
        task.setTitle("Test Task");
        task.setDescription("Task Description");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.HIGH);
        task.setProject(project);
        task.setMemberAssignments(new HashSet<>());
    }

    @Test
    void shouldCreateTask() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto taskDto = new TaskDto(null, "Test Task", "Task Description", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDateTime.now(), 1L);
        TaskDto createdTask = taskService.createTask(taskDto);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldUpdateTask() throws NoSuchFieldException, IllegalAccessException {

        Project project = new Project();
        setEntityId(project, 1L);

        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.TODO);
        existingTask.setPriority(TaskPriority.LOW);
        existingTask.setDeadline(LocalDateTime.now().plusDays(1));
        existingTask.setProject(project);


        TaskDto taskDto = new TaskDto(1L, "Updated Title", "Updated Description", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, LocalDateTime.now().plusDays(3), 1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDto updatedTask = taskService.updateTask(1L, taskDto);

        assertNotNull(updatedTask);
        assertEquals("Updated Title", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(TaskPriority.HIGH, updatedTask.getPriority());
        assertEquals(1L, updatedTask.getProjectId());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldUpdateTaskStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto updatedTask = taskService.updateTaskStatus(1L, "DONE");

        assertNotNull(updatedTask);
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }

    @Test
    void shouldAssignMemberToTask() throws NoSuchFieldException, IllegalAccessException {
        ProjectMember member = new ProjectMember();
        setEntityId(member, 1L);
        member.setProject(project);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(projectMemberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberTaskAssignmentRepository.existsByTaskAndMember(task, member)).thenReturn(false);

        TaskWithAssigneesDto updatedTask = taskService.assignMemberToTask(1L, 1L);

        assertNotNull(updatedTask);
        verify(memberTaskAssignmentRepository, times(1)).save(any(MemberTaskAssignment.class));
    }

    @Test
    void shouldDeleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        boolean result = taskService.deleteTask(1L);

        assertTrue(result);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldCreateLabel() throws NoSuchFieldException, IllegalAccessException {
        TaskLabel label = new TaskLabel();
        setEntityId(label, 1L);
        label.setTitle("Bug");

        when(taskLabelRepository.save(any(TaskLabel.class))).thenReturn(label);

        TaskLabelDto labelDto = new TaskLabelDto(null, "Bug");
        TaskLabelDto createdLabel = taskService.createLabel(labelDto);

        assertNotNull(createdLabel);
        assertEquals("Bug", createdLabel.getTitle());
        verify(taskLabelRepository, times(1)).save(any(TaskLabel.class));
    }

    @Test
    void shouldUpdateLabel() throws NoSuchFieldException, IllegalAccessException {
        TaskLabel label = new TaskLabel();
        setEntityId(label, 1L);
        label.setTitle("Bug");

        when(taskLabelRepository.findById(1L)).thenReturn(Optional.of(label));
        when(taskLabelRepository.save(any(TaskLabel.class))).thenReturn(label);

        TaskLabelDto updatedLabel = taskService.updateLabel(1L, new TaskLabelDto("Feature", "blue"));


        assertNotNull(updatedLabel);
        assertEquals("Feature", updatedLabel.getTitle());
        verify(taskLabelRepository, times(1)).save(any(TaskLabel.class));
    }

    @Test
    void shouldDeleteLabel() {
        when(taskLabelRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskLabelRepository).deleteById(1L);

        boolean result = taskService.deleteLabel(1L);

        assertTrue(result);
        verify(taskLabelRepository, times(1)).deleteById(1L);
    }

    private void setEntityId(Object entity, Long id) throws NoSuchFieldException, IllegalAccessException {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
