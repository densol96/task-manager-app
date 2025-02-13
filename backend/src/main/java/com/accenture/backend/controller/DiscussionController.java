package com.accenture.backend.controller;

import com.accenture.backend.entity.ProjectDiscussion;
import com.accenture.backend.entity.ProjectDiscussionMessage;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.Project;
import com.accenture.backend.service.serviceimpl.DiscussionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discussion")
public class DiscussionController {

    @Autowired
    private DiscussionServiceImpl discussionService;

    /*@PostMapping("/create")
    public ProjectDiscussion createDiscussion(@RequestParam String title,
                                              @RequestParam Integer postedById,
                                              @RequestParam Integer projectId) {
        // Replace with actual lookup logic for ProjectMember and Project
        ProjectMember postedBy = new ProjectMember(postedById);  // Assuming you find this from DB
        Project project = new Project(projectId);  // Assuming you find this from DB
        return discussionService.createDiscussion(title, postedBy, project);
    }*/

    @GetMapping("/{discussionId}/messages")
    public List<ProjectDiscussionMessage> getMessagesForDiscussion(@PathVariable Integer discussionId) {
        return discussionService.getMessagesForDiscussion(discussionId);
    }

    /*@PostMapping("/{discussionId}/message")
    public ProjectDiscussionMessage addMessageToDiscussion(@PathVariable Integer discussionId,
                                                           @RequestParam String message,
                                                           @RequestParam Integer postedById) {
        // Replace with actual lookup logic for ProjectMember
        ProjectMember postedBy = new ProjectMember(postedById);  // Assuming you find this from DB
        return discussionService.addMessageToDiscussion(discussionId, message, postedBy);
    }*/
}
