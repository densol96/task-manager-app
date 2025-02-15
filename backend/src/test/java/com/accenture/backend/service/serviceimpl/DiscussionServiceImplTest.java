package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.entity.ProjectDiscussion;
import com.accenture.backend.entity.ProjectDiscussionMessage;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.exception.EntityNotFoundException;
import com.accenture.backend.repository.ProjectDiscussionRepository;
import com.accenture.backend.repository.ProjectDiscussionMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceImplTest {

    @Mock
    private ProjectDiscussionRepository projectDiscussionRepository;

    @Mock
    private ProjectDiscussionMessageRepository projectDiscussionMessageRepository;

    @InjectMocks
    private DiscussionServiceImpl discussionService;

    private ProjectDiscussion projectDiscussion;
    private ProjectMember projectMember;
    private ProjectDiscussionMessage projectDiscussionMessage;

    @BeforeEach
    void setUp() {
        projectMember = new ProjectMember();

        projectDiscussion = new ProjectDiscussion();
        projectDiscussion.setTitle("Test Discussion");
        projectDiscussion.setPostedBy(projectMember);
        projectDiscussion.setCreatedAt(LocalDateTime.now());

        projectDiscussionMessage = new ProjectDiscussionMessage();
        projectDiscussionMessage.setMessage("Test Message");
        projectDiscussionMessage.setPostedBy(projectMember);
        projectDiscussionMessage.setDiscussion(projectDiscussion);
        projectDiscussionMessage.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createDiscussion_ShouldReturnCreatedDiscussion() {
        when(projectDiscussionRepository.save(any(ProjectDiscussion.class))).thenAnswer(invocation -> {
            ProjectDiscussion savedDiscussion = invocation.getArgument(0);
            setIdUsingReflection(savedDiscussion, 1L);
            return savedDiscussion;
        });

        ProjectDiscussion createdDiscussion = discussionService.createDiscussion("Test Discussion", projectMember, projectDiscussion);

        assertNotNull(createdDiscussion);
        assertEquals("Test Discussion", createdDiscussion.getTitle());
        assertNotNull(createdDiscussion.getId());
        verify(projectDiscussionRepository, times(1)).save(any(ProjectDiscussion.class));
    }

    @Test
    void getMessagesForDiscussion_ShouldReturnMessages() {
        when(projectDiscussionMessageRepository.findByDiscussionId(anyInt())).thenReturn(List.of(projectDiscussionMessage));

        List<ProjectDiscussionMessage> messages = discussionService.getMessagesForDiscussion(1L);

        assertFalse(messages.isEmpty());
        assertEquals(1, messages.size());
        assertEquals("Test Message", messages.get(0).getMessage());
        verify(projectDiscussionMessageRepository, times(1)).findByDiscussionId(1);
    }

    @Test
    void addMessageToDiscussion_ShouldAddMessage_WhenDiscussionExists() {
        when(projectDiscussionRepository.findById(anyInt())).thenReturn(Optional.of(projectDiscussion));
        when(projectDiscussionMessageRepository.save(any(ProjectDiscussionMessage.class))).thenAnswer(invocation -> {
            ProjectDiscussionMessage savedMessage = invocation.getArgument(0);
            setIdUsingReflection(savedMessage, 1L);
            return savedMessage;
        });

        ProjectDiscussionMessage savedMessage = discussionService.addMessageToDiscussion(1L, "New Message", projectMember);

        assertNotNull(savedMessage);
        assertEquals("New Message", savedMessage.getMessage());
        assertNotNull(savedMessage.getId());
        verify(projectDiscussionRepository, times(1)).findById(1);
        verify(projectDiscussionMessageRepository, times(1)).save(any(ProjectDiscussionMessage.class));
    }

    @Test
    void addMessageToDiscussion_ShouldThrowException_WhenDiscussionNotFound() {
        when(projectDiscussionRepository.findById(anyInt())).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                discussionService.addMessageToDiscussion(1L, "New Message", projectMember));

        assertEquals("Discussion not found with ID: 1", thrown.getMessage());
        verify(projectDiscussionRepository, times(1)).findById(1);
        verifyNoInteractions(projectDiscussionMessageRepository);
    }

    @Test
    void deleteMessage_ShouldDeleteMessage_WhenMessageExists() {
        when(projectDiscussionMessageRepository.findById(anyInt())).thenReturn(Optional.of(projectDiscussionMessage));
        doNothing().when(projectDiscussionMessageRepository).delete(any(ProjectDiscussionMessage.class));

        assertDoesNotThrow(() -> discussionService.deleteMessage(1L));

        verify(projectDiscussionMessageRepository, times(1)).findById(1);
        verify(projectDiscussionMessageRepository, times(1)).delete(projectDiscussionMessage);
    }

    @Test
    void deleteMessage_ShouldThrowException_WhenMessageNotFound() {
        when(projectDiscussionMessageRepository.findById(anyInt())).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                discussionService.deleteMessage(1L));

        assertEquals("Message not found with ID: 1", thrown.getMessage());
        verify(projectDiscussionMessageRepository, times(1)).findById(1);
        verify(projectDiscussionMessageRepository, never()).delete(any(ProjectDiscussionMessage.class));
    }

    private void setIdUsingReflection(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID using reflection", e);
        }
    }
}
