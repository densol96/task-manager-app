package com.accenture.backend.controller;

import com.accenture.backend.entity.ProjectDiscussion;
import com.accenture.backend.entity.ProjectDiscussionMessage;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.Project;
import com.accenture.backend.exception.EntityNotFoundException;
import com.accenture.backend.repository.ProjectMemberRepository;
import com.accenture.backend.repository.ProjectRepository;
import com.accenture.backend.service.serviceimpl.DiscussionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionControllerTest {

    @Mock
    private DiscussionServiceImpl discussionService;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private DiscussionController discussionController;

    private ProjectMember mockMember;
    private Project mockProject;
    private ProjectDiscussion mockDiscussion;
    private ProjectDiscussionMessage mockMessage;

    @BeforeEach
    void setUp() throws Exception {
        mockMember = new ProjectMember();
        setEntityId(mockMember, 1L);

        mockProject = new Project();
        setEntityId(mockProject, 1L);

        mockDiscussion = new ProjectDiscussion();
        setEntityId(mockDiscussion, 1L);

        mockMessage = new ProjectDiscussionMessage();
        setEntityId(mockMessage, 1L);
    }

    private void setEntityId(Object entity, Long id) throws Exception {
        java.lang.reflect.Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }

    @Test
    void testCreateDiscussion_Success() {
        when(projectMemberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(discussionService.createDiscussion(anyString(), any(ProjectMember.class), any(ProjectDiscussion.class)))
                .thenReturn(mockDiscussion);

        ResponseEntity<ProjectDiscussion> response = discussionController.createDiscussion("New Discussion", 1L, 1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testCreateDiscussion_ProjectMemberNotFound() {
        when(projectMemberRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                discussionController.createDiscussion("New Discussion", 1L, 1L));

        assertEquals("ProjectMember not found with ID: 1", exception.getMessage());
    }

    @Test
    void testCreateDiscussion_ProjectNotFound() {
        when(projectMemberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                discussionController.createDiscussion("New Discussion", 1L, 1L));

        assertEquals("Project not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetMessages_Success() {
        when(discussionService.getMessagesForDiscussion(1L)).thenReturn(List.of(mockMessage));

        ResponseEntity<List<ProjectDiscussionMessage>> response = discussionController.getMessages(1L);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testAddMessage_Success() {
        when(projectMemberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(discussionService.addMessageToDiscussion(1L, "Hello", mockMember)).thenReturn(mockMessage);

        ResponseEntity<ProjectDiscussionMessage> response = discussionController.addMessage(1L, "Hello", 1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testAddMessage_ProjectMemberNotFound() {
        when(projectMemberRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                discussionController.addMessage(1L, "Hello", 1L));

        assertEquals("ProjectMember not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteMessage_Success() {
        doNothing().when(discussionService).deleteMessage(1L);

        ResponseEntity<String> response = discussionController.deleteMessage(1L);

        assertEquals("Message deleted successfully.", response.getBody());
    }
}
