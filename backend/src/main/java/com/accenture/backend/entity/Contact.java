package com.accenture.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contacts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "contact_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    private User contact;
}
