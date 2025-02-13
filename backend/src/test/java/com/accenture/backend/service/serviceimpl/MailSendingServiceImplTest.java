package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.enums.report.ReportType;
import com.accenture.backend.exception.MailServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MailSendingServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private MailSendingServiceImpl mailSendingService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void shouldSendCodeEmailSuccessfully() throws Exception {
        String toEmail = "test@example.com";
        String code = "12345";
        String generatedHtml = "<html>Approval Code: 12345</html>";

        when(templateEngine.process(eq("templateHtml"), any(Context.class))).thenReturn(generatedHtml);

        mailSendingService.sendCode(toEmail, code);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        MimeMessage sentMessage = captor.getValue();

        assertNotNull(sentMessage);
        verify(templateEngine, times(1)).process(eq("templateHtml"), any(Context.class));
    }

    @Test
    void shouldSendBanNotificationSuccessfully() throws Exception {
        String toEmail = "banned@example.com";
        ReportType reportType = ReportType.RULES_VIOLATION;
        String generatedHtml = "<html>Reason: rules violation</html>";

        when(templateEngine.process(eq("banTemplateHtml"), any(Context.class))).thenReturn(generatedHtml);

        mailSendingService.sendBanNotification(toEmail, reportType);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        MimeMessage sentMessage = captor.getValue();

        assertNotNull(sentMessage);
        verify(templateEngine, times(1)).process(eq("banTemplateHtml"), any(Context.class));
    }

    @Test
    void shouldThrowMailServiceExceptionWhenSendCodeFails() {
        String toEmail = "fail@example.com";
        String code = "54321";

        when(templateEngine.process(eq("templateHtml"), any(Context.class))).thenReturn("<html>Error</html>");
        doThrow(new RuntimeException("Mail sending error")).when(mailSender).send(any(MimeMessage.class));

        MailServiceException exception = assertThrows(MailServiceException.class, () -> mailSendingService.sendCode(toEmail, code));

        assertTrue(exception.getMessage().contains("Exception occurred while sending verification email to"));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldThrowMailServiceExceptionWhenSendBanNotificationFails() {
        String toEmail = "banfail@example.com";
        ReportType reportType = ReportType.SPAM;

        when(templateEngine.process(eq("banTemplateHtml"), any(Context.class))).thenReturn("<html>Error</html>");
        doThrow(new RuntimeException("Mail sending error")).when(mailSender).send(any(MimeMessage.class));

        MailServiceException exception = assertThrows(MailServiceException.class, () -> mailSendingService.sendBanNotification(toEmail, reportType));

        assertTrue(exception.getMessage().contains("Exception occurred while sending ban details email to"));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}

