import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.accenture.backend.dto.response.BasicMessageDto;
import com.accenture.backend.dto.response.HasUnreadDto;
import com.accenture.backend.dto.response.NotificationShortDto;
import com.accenture.backend.entity.Notification;
import com.accenture.backend.entity.User;
import com.accenture.backend.repository.NotificationRepository;
import com.accenture.backend.service.NotificationService;
import com.accenture.backend.service.UserService;
import com.accenture.backend.service.serviceimpl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserService userService;

    @InjectMocks
    private com.accenture.backend.service.serviceimpl.NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Test Title");
        notification.setMessage("Test Message");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setHasBeenRead(false);
    }

    @Test
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createNotification(user, "Test Title", "Test Message");

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Message", result.getMessage());
        assertEquals(user, result.getUser());
        assertFalse(result.getHasBeenRead());
        assertNotNull(result.getCreatedAt());

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetAllUserNotifications() {
        when(userService.getLoggedInUserId()).thenReturn(1L);
        when(notificationRepository.countAllByUserId(1L)).thenReturn(1L);
        when(notificationRepository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationShortDto> result = notificationService.getAllUserNotifications(1, 10);

        assertFalse(result.isEmpty());
        verify(notificationRepository, times(1)).findAllByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    void testGetUserUnreadNotifications() {
        when(userService.getLoggedInUserId()).thenReturn(1L);
        when(notificationRepository.countAllByUserIdAndHasBeenRead(1L, false)).thenReturn(1L);
        when(notificationRepository.findAllByUserIdAndHasBeenRead(anyLong(), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationShortDto> result = notificationService.getUserUnreadNotifications(1, 10);

        assertFalse(result.isEmpty());
        verify(notificationRepository, times(1)).findAllByUserIdAndHasBeenRead(anyLong(), eq(false), any(Pageable.class));
    }

    @Test
    void testGetUserReadNotifications() {
        when(userService.getLoggedInUserId()).thenReturn(1L);
        when(notificationRepository.countAllByUserIdAndHasBeenRead(1L, true)).thenReturn(1L);
        when(notificationRepository.findAllByUserIdAndHasBeenRead(anyLong(), eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationShortDto> result = notificationService.getUserReadNotifications(1, 10);

        assertFalse(result.isEmpty());
        verify(notificationRepository, times(1)).findAllByUserIdAndHasBeenRead(anyLong(), eq(true), any(Pageable.class));
    }

    @Test
    void testMarkAsRead() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(userService.getLoggedInUserId()).thenReturn(1L);

        NotificationShortDto result = notificationService.markAsRead(1L);

        assertTrue(result.getHasBeenRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testUserHasUnreadNotifications() {
        when(userService.getLoggedInUserId()).thenReturn(1L);
        when(notificationRepository.existsByUserIdAndHasBeenRead(1L, false)).thenReturn(true);

        HasUnreadDto result = notificationService.userHasUnreadNotifications();

        assertTrue(result.getHasUnread());
        verify(notificationRepository, times(1)).existsByUserIdAndHasBeenRead(anyLong(), eq(false));
    }

    @Test
    void testDeleteNotification() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        when(userService.getLoggedInUserId()).thenReturn(1L);

        BasicMessageDto result = notificationService.deleteNotification(1L);

        assertEquals("Notification with the id of 1 has been succefully deleted.", result.getMessage());
        verify(notificationRepository, times(1)).delete(any(Notification.class));
    }
}
