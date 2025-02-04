package com.accenture.backend.notifications.service;

import com.accenture.backend.entity.User; //
import com.accenture.backend.notifications.entity.Notification;
import com.accenture.backend.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotificationsByUser(User user) {
        return notificationRepository.findByUserAndHasBeenRead(user, false);
    }

    public Notification createNotification(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setHasBeenRead(false);

        // Send email notification
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("New Notification: " + title);
        email.setText(message);
        mailSender.send(email);

        return notificationRepository.save(notification);
    }

    public Notification markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setHasBeenRead(true);
        return notificationRepository.save(notification);
    }
}