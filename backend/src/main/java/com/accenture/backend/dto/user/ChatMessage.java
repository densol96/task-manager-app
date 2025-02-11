package com.accenture.backend.dto.user;

import lombok.Data;

@Data
public class ChatMessage {
    private Long senderId;
    private Long receiverId;
    private String content;
}
