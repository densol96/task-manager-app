package com.accenture.backend.controller;

import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.HasUnreadDto;
import com.accenture.backend.dto.response.NotificationShortDto;
import com.accenture.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing user notifications.
 *
 * Provides endpoints to:
 * - Retrieve all notifications (paginated)
 * - Retrieve read/unread notifications
 * - Mark notifications as read
 * - Check unread notifications
 * - Delete notifications
 *
 * @author Olena
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification API", description = "Endpoints for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get all notifications", description = "Retrieves all notifications for the authenticated user with pagination support.")
    @GetMapping("/all")
    public ResponseEntity<Page<NotificationShortDto>> getAllNotificationsByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        return new ResponseEntity<>(notificationService.getAllUserNotifications(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Get read notifications", description = "Retrieves all read notifications for the authenticated user with pagination support.")
    @GetMapping("/read")
    public ResponseEntity<Page<NotificationShortDto>> getReadNotificationsByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        return new ResponseEntity<>(notificationService.getUserReadNotifications(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Get unread notifications", description = "Retrieves all unread notifications for the authenticated user with pagination support.")
    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationShortDto>> getUnreadNotificationsByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        return new ResponseEntity<>(notificationService.getUserUnreadNotifications(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read by its ID.")
    @PutMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<NotificationShortDto> markAsRead(@PathVariable Long notificationId) {
        return new ResponseEntity<>(notificationService.markAsRead(notificationId), HttpStatus.OK);
    }

    @Operation(summary = "Check unread notifications", description = "Checks if the authenticated user has any unread notifications.")
    @GetMapping("/has-unread-messages")
    public ResponseEntity<HasUnreadDto> userHasUnreadNotifications() {
        return new ResponseEntity<>(notificationService.userHasUnreadNotifications(), HttpStatus.OK);
    }

    @Operation(summary = "Delete a notification", description = "Deletes a specific notification by its ID.")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<BasicMessageDto> deleteNotification(@PathVariable Long notificationId) {
        return new ResponseEntity<>(notificationService.deleteNotification(notificationId), HttpStatus.OK);
    }
}
