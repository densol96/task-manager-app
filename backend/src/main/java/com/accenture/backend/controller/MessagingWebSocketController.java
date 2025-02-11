package com.accenture.backend.controller;

import com.accenture.backend.dto.user.ChatMessage;
import com.accenture.backend.service.serviceimpl.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessagingWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessagingService messagingService;

    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessage message) {
        messagingService.sendMessage(message.getSenderId(), message.getReceiverId(), message.getContent());

        messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(),
                "/queue/messages",
                message
        );
    }
}
