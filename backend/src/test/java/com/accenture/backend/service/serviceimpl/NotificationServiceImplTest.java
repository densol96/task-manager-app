import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.accenture.backend.dto.response.NotificationShortDto;
import com.accenture.backend.entity.Notification;
import com.accenture.backend.entity.User;
import com.accenture.backend.repository.NotificationRepository;
import com.accenture.backend.service.UserService;
import com.accenture.backend.service.serviceimpl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotification() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Test Title");
        notification.setMessage("Test Message");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setHasBeenRead(false);

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        Notification result = notificationService.createNotification(user, "Test Title", "Test Message");

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Message", result.getMessage());
        assertEquals(user, result.getUser());
    }

    @Test
    void testGetAllUserNotifications() {
        Long userId = 1L;
        when(userService.getLoggedInUserId()).thenReturn(userId);
        Pageable pageable = PageRequest.of(0, 5);
        Notification notification = new Notification();
        notification.setTitle("Title");
        notification.setMessage("Message");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setHasBeenRead(false);
        Page<Notification> page = new PageImpl<>(List.of(notification));
        when(notificationRepository.findAllByUserId(eq(userId), any(Pageable.class))).thenReturn(page);
        Page<NotificationShortDto> result = notificationService.getAllUserNotifications(1, 5);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}