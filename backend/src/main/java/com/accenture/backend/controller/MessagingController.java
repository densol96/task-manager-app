package com.accenture.backend.controller;

import com.accenture.backend.dto.user.MessagePrivacyDto;
import com.accenture.backend.entity.User;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.service.serviceimpl.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messaging")
@RequiredArgsConstructor
public class MessagingController {

    private final UserRepository userRepository;
    private final MessagingService messagingService;

    @PutMapping("/privacy")
    public ResponseEntity<?> updateMessagePrivacy(@RequestParam Long userId,
                                                  @RequestBody MessagePrivacyDto privacyDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setMessagePrivacy(privacyDto.getMessagePrivacy());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestParam Long senderId,
                                         @RequestParam Long receiverId,
                                         @RequestParam String content) {
        messagingService.sendMessage(senderId, receiverId, content);
        return ResponseEntity.ok().build();
    }
}
