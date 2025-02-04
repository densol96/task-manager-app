package com.accenture.backend.notifications.controller;

import com.accenture.backend.entity.User;
import com.accenture.backend.notifications.entity.Notification;
import com.accenture.backend.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable User user) {
        return notificationService.getNotificationsByUser(user);
    }

    @GetMapping("/user/{userId}/unread")
    public List<Notification> getUnreadNotificationsByUser(@PathVariable User user) {
        return notificationService.getUnreadNotificationsByUser(user);
    }

    @PostMapping
    public Notification createNotification(@RequestParam User user, @RequestParam String title, @RequestParam String message) {
        return notificationService.createNotification(user, title, message);
    }

    @PutMapping("/{notificationId}/mark-as-read")
    public Notification markAsRead(@PathVariable Integer notificationId) {
        return notificationService.markAsRead(notificationId);
    }
}