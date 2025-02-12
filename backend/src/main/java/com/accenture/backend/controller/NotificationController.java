package com.accenture.backend.controller;

import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.HasUnreadDto;
import com.accenture.backend.dto.response.NotificationShortDto;
import com.accenture.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/all")
    public ResponseEntity<Page<NotificationShortDto>> geAllNotificationsByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        return new ResponseEntity<>(notificationService.getAllUserNotifications(page, size), HttpStatus.OK);
    }

    @GetMapping("/read")
    public ResponseEntity<Page<NotificationShortDto>> getReadNotificationsByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        return new ResponseEntity<>(notificationService.getUserReadNotifications(page, size), HttpStatus.OK);
    }

    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationShortDto>> getUnreadNotificationsByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        return new ResponseEntity<>(notificationService.getUserUnreadNotifications(page, size), HttpStatus.OK);
    }

    @PutMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<NotificationShortDto> markAsRead(@PathVariable Long notificationId) {
        return new ResponseEntity<>(notificationService.markAsRead(notificationId), HttpStatus.OK);
    }

    @GetMapping("/has-unread-messages")
    public ResponseEntity<HasUnreadDto> userHasUnreadNotifications() {
        return new ResponseEntity<>(notificationService.userHasUnreadNotifications(), HttpStatus.OK);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<BasicMessageDto> deleteNotification(@PathVariable Long notificationId) {
        return new ResponseEntity<>(notificationService.deleteNotification(notificationId), HttpStatus.OK);
    }

    // @PostMapping
    // public Notification createNotification(@RequestParam User user, @RequestParam
    // String title,
    // @RequestParam String message) {
    // return notificationService.createNotification(user, title, message);
    // }

}