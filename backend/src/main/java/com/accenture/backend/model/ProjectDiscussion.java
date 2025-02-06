package com.accenture.backend.model;

import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectMember;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class ProjectDiscussion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "posted_by_id")
    private ProjectMember postedBy;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}