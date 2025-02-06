package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.entity.ProjectDiscussion;
import com.accenture.backend.entity.ProjectDiscussionMessage;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.Project;
import com.accenture.backend.repository.ProjectDiscussionRepository;
import com.accenture.backend.repository.ProjectDiscussionMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussionServiceImpl {

    @Autowired
    private ProjectDiscussionRepository projectDiscussionRepository;

    @Autowired
    private ProjectDiscussionMessageRepository projectDiscussionMessageRepository;

    public ProjectDiscussion createDiscussion(String title, ProjectMember postedBy, Project project) {
        ProjectDiscussion discussion = new ProjectDiscussion();
        discussion.setTitle(title);
        discussion.setPostedBy(postedBy);
        discussion.setProject(project);
        discussion.setCreatedAt(new java.util.Date());
        return projectDiscussionRepository.save(discussion);
    }

    public List<ProjectDiscussionMessage> getMessagesForDiscussion(Integer discussionId) {
        return projectDiscussionMessageRepository.findByDiscussionId(discussionId);
    }

    public ProjectDiscussionMessage addMessageToDiscussion(Integer discussionId, String message, ProjectMember postedBy) {
        ProjectDiscussionMessage discussionMessage = new ProjectDiscussionMessage();
        discussionMessage.setMessage(message);
        discussionMessage.setPostedBy(postedBy);
        discussionMessage.setDiscussion(new ProjectDiscussion(discussionId)); // Assuming you set the discussion by id
        discussionMessage.setCreatedAt(new java.util.Date());
        return projectDiscussionMessageRepository.save(discussionMessage);
    }
}
