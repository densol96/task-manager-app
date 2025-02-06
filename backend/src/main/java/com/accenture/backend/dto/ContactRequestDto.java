package com.accenture.backend.dto;

import com.accenture.backend.entity.ContactRequestStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private ContactRequestStatus status;
    private LocalDateTime createdAt;
}
