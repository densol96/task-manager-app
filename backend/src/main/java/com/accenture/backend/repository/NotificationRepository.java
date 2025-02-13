package com.accenture.backend.repository;

import com.accenture.backend.entity.Notification;
import com.accenture.backend.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Long countAllByUserId(Long userId);

    Long countAllByUserIdAndHasBeenRead(Long userId, Boolean hasBeenRead);

    Boolean existsByUserIdAndHasBeenRead(Long userId, Boolean hasBeenRead);

    Page<Notification> findAllByUserId(Long userId, Pageable pageable);

    Page<Notification> findAllByUserIdAndHasBeenRead(Long userId, Boolean hasBeenRead, Pageable pageable);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByUserAndHasBeenRead(User user, boolean hasBeenRead);

}