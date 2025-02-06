package com.accenture.backend.service;

import com.accenture.backend.model.ProjectDiscussion;
import com.accenture.backend.model.ProjectDiscussionMessage;
import com.accenture.backend.repository.ProjectDiscussionRepository;
import com.accenture.backend.repository.ProjectDiscussionMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussionService {

    @Autowired
    private ProjectDiscussionRepository discussionRepository;

    @Autowired
    private ProjectDiscussionMessageRepository messageRepository;

    public ProjectDiscussion createDiscussion(ProjectDiscussion discussion) {
        return discussionRepository.save(discussion);
    }

    public ProjectDiscussionMessage addMessageToDiscussion(ProjectDiscussionMessage message) {
        return messageRepository.save(message);
    }

    public List<ProjectDiscussionMessage> getMessagesByDiscussionId(Integer discussionId) {
        return messageRepository.findByDiscussionId(discussionId);
    }
}