package com.accenture.backend.controller;

import com.accenture.backend.model.ProjectDiscussion;
import com.accenture.backend.model.ProjectDiscussionMessage;
import com.accenture.backend.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @PostMapping
    public ProjectDiscussion createDiscussion(@RequestBody ProjectDiscussion discussion) {
        return discussionService.createDiscussion(discussion);
    }

    @PostMapping("/{discussionId}/messages")
    public ProjectDiscussionMessage addMessage(@PathVariable Integer discussionId, @RequestBody ProjectDiscussionMessage message) {
        return discussionService.addMessageToDiscussion(message);
    }

    @GetMapping("/{discussionId}/messages")
    public List<ProjectDiscussionMessage> getMessages(@PathVariable Integer discussionId) {
        return discussionService.getMessagesByDiscussionId(discussionId);
    }
}