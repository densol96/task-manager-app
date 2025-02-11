package com.accenture.backend.entity;

import com.accenture.backend.enums.ContactRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactRequestStatus status = ContactRequestStatus.PENDING;

    private LocalDateTime createdAt;

    @PrePersist
    public void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
