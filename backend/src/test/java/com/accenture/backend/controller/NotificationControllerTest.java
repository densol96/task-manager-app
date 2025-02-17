package com.accenture.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

//@ExtendWith(MockitoExtension.class)
//class NotificationControllerTest {
//
//    @Mock
//    private NotificationService notificationService;
//
//    @InjectMocks
//    private NotificationController notificationController;
//
//    private NotificationShortDto notificationDto;
//
//    @BeforeEach
//    void setUp() {
//        notificationDto = new NotificationShortDto(1L, "Test Title", "Test Message", LocalDateTime.now(), false);
//    }
//
//    @Test
//    void testGetAllNotificationsByUser() {
//        Page<NotificationShortDto> page = new PageImpl<>(List.of(notificationDto));
//        when(notificationService.getAllUserNotifications(any(), any())).thenReturn(page);
//
//        ResponseEntity<Page<NotificationShortDto>> response = notificationController.geAllNotificationsByUser(0, 5);
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isNotNull().hasSize(1);
//    }
//
//    @Test
//    void testGetReadNotificationsByUser() {
//        Page<NotificationShortDto> page = new PageImpl<>(List.of(notificationDto));
//        when(notificationService.getUserReadNotifications(any(), any())).thenReturn(page);
//
//        ResponseEntity<Page<NotificationShortDto>> response = notificationController.getReadNotificationsByUser(0, 5);
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isNotNull().hasSize(1);
//    }
//
//    @Test
//    void testGetUnreadNotificationsByUser() {
//        Page<NotificationShortDto> page = new PageImpl<>(List.of(notificationDto));
//        when(notificationService.getUserUnreadNotifications(any(), any())).thenReturn(page);
//
//        ResponseEntity<Page<NotificationShortDto>> response = notificationController.getUnreadNotificationsByUser(0, 5);
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isNotNull().hasSize(1);
//    }
//
//    @Test
//    void testMarkAsRead() {
//        when(notificationService.markAsRead(eq(1L))).thenReturn(notificationDto);
//
//        ResponseEntity<NotificationShortDto> response = notificationController.markAsRead(1L);
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getId()).isEqualTo(1L);
//    }
//
//    @Test
//    void testUserHasUnreadNotifications() {
//        HasUnreadDto hasUnreadDto = new HasUnreadDto(true);
//        when(notificationService.userHasUnreadNotifications()).thenReturn(hasUnreadDto);
//
//        ResponseEntity<HasUnreadDto> response = notificationController.userHasUnreadNotifications();
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getHasUnread()).isTrue();
//    }
//
//    @Test
//    void testDeleteNotification() {
//        BasicMessageDto basicMessageDto = new BasicMessageDto("Deleted Successfully");
//        when(notificationService.deleteNotification(eq(1L))).thenReturn(basicMessageDto);
//
//        ResponseEntity<BasicMessageDto> response = notificationController.deleteNotification(1L);
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getMessage()).isEqualTo("Deleted Successfully");
//    }
//}
