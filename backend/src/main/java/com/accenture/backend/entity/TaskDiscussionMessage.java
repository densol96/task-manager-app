package com.accenture.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TaskDiscussionMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private ProjectMember author;

    @Column(nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    private LocalDateTime postedAt = LocalDateTime.now();
}

