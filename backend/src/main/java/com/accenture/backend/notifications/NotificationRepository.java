package com.accenture.backend.notifications.repository;

import com.accenture.backend.entity.User;
import com.accenture.backend.notifications.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndHasBeenRead(User user, boolean hasBeenRead);
}