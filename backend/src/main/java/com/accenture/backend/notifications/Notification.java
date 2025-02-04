package com.accenture.backend.notifications.entity;

import com.accenture.backend.entity.User; // Correct import statement
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean hasBeenRead;
}