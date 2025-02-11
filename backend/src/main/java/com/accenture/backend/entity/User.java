package com.accenture.backend.entity;

import com.accenture.backend.enums.MessagePrivacy;
import com.accenture.backend.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String email;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessagePrivacy messagePrivacy = MessagePrivacy.ANYONE;
}
