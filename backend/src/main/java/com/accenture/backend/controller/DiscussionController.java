package com.accenture.backend.controller;

import com.accenture.backend.entity.ProjectDiscussion;
import com.accenture.backend.entity.ProjectDiscussionMessage;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.Project;
import com.accenture.backend.exception.EntityNotFoundException;
import com.accenture.backend.repository.ProjectMemberRepository;
import com.accenture.backend.repository.ProjectRepository;
import com.accenture.backend.service.serviceimpl.DiscussionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing project discussions and messages.
 *
 * Provides endpoints to:
 * - Create a new discussion
 * - Retrieve messages for a discussion
 * - Add a message to a discussion
 * - Delete a message from a discussion
 *
 * @author Olena
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/discussions")
@Tag(name = "Discussion API", description = "Endpoints for managing project discussions and messages")
public class DiscussionController {

    private final DiscussionServiceImpl discussionService;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;

    public DiscussionController(DiscussionServiceImpl discussionService,
                                ProjectMemberRepository projectMemberRepository,
                                ProjectRepository projectRepository) {
        this.discussionService = discussionService;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
    }

    @Operation(summary = "Create a new discussion", description = "Creates a new discussion within a specified project")
    @PostMapping("/create")
    public ResponseEntity<ProjectDiscussion> createDiscussion(@RequestParam String title,
                                                              @RequestParam Long postedById,
                                                              @RequestParam Long projectId) {
        ProjectMember postedBy = projectMemberRepository.findById(postedById)
                .orElseThrow(() -> new EntityNotFoundException("ProjectMember not found with ID: " + postedById));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        ProjectDiscussion projectDiscussion = new ProjectDiscussion();
        projectDiscussion.setProject(project);

        ProjectDiscussion discussion = discussionService.createDiscussion(title, postedBy, projectDiscussion);
        return ResponseEntity.ok(discussion);
    }

    @Operation(summary = "Get messages from a discussion", description = "Retrieves all messages for a specific discussion")
    @GetMapping("/{discussionId}/messages")
    public ResponseEntity<List<ProjectDiscussionMessage>> getMessages(@PathVariable Long discussionId) {
        List<ProjectDiscussionMessage> messages = discussionService.getMessagesForDiscussion(discussionId);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Add a message to a discussion", description = "Adds a new message to an existing discussion")
    @PostMapping("/{discussionId}/messages/add")
    public ResponseEntity<ProjectDiscussionMessage> addMessage(@PathVariable Long discussionId,
                                                               @RequestParam String message,
                                                               @RequestParam Long postedById) {
        ProjectMember postedBy = projectMemberRepository.findById(postedById)
                .orElseThrow(() -> new EntityNotFoundException("ProjectMember not found with ID: " + postedById));

        ProjectDiscussionMessage discussionMessage = discussionService.addMessageToDiscussion(discussionId, message, postedBy);
        return ResponseEntity.ok(discussionMessage);
    }

    @Operation(summary = "Delete a message", description = "Deletes a specific message from a discussion by ID")
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long messageId) {
        discussionService.deleteMessage(messageId);
        return ResponseEntity.ok("Message deleted successfully.");
    }
}