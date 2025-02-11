package com.accenture.backend.dto.user;

import com.accenture.backend.enums.ContactRequestStatus;
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
