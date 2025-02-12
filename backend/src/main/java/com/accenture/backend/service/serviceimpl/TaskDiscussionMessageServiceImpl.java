package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.entity.Task;
import com.accenture.backend.entity.TaskDiscussionMessage;
import com.accenture.backend.repository.TaskDiscussionMessageRepository;
import com.accenture.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskDiscussionMessageServiceImpl {

    @Autowired
    private TaskDiscussionMessageRepository taskDiscussionMessageRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Method to add a new comment on a task
    public TaskDiscussionMessage addComment(Long taskId, String message) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        TaskDiscussionMessage newMessage = new TaskDiscussionMessage();
        newMessage.setTask(task);
        newMessage.setMessage(message);
        newMessage.setPostedAt(LocalDateTime.now());
        return taskDiscussionMessageRepository.save(newMessage);
    }

    // Method to fetch all comments for a task
    public Iterable<TaskDiscussionMessage> getComments(Long taskId) {
        return taskDiscussionMessageRepository.findByTaskId(taskId);
    }
}
