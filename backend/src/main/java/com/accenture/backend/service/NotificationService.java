package com.accenture.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.HasUnreadDto;
import com.accenture.backend.dto.response.NotificationShortDto;
import com.accenture.backend.entity.Notification;
import com.accenture.backend.entity.User;

public interface NotificationService {
    Notification createNotification(User user, String title, String message);

    HasUnreadDto userHasUnreadNotifications();

    Page<NotificationShortDto> getAllUserNotifications(Integer page, Integer size);

    Page<NotificationShortDto> getUserUnreadNotifications(Integer page, Integer size);

    Page<NotificationShortDto> getUserReadNotifications(Integer page, Integer size);

    BasicMessageDto deleteNotification(Long notificationId);

    NotificationShortDto markAsRead(Long notificationId);
}
