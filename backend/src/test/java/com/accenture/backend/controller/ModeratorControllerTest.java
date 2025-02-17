package com.accenture.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.accenture.backend.dto.report.InternalReportDto;
import com.accenture.backend.dto.report.ReportInfoDto;
import com.accenture.backend.dto.report.ReportSearchDto;
import com.accenture.backend.dto.report.ReportUpdateDto;
import com.accenture.backend.dto.report.ReportValidationResultDto;
import com.accenture.backend.dto.report.ShortReportInfoDto;
import com.accenture.backend.enums.Role;
import com.accenture.backend.enums.UserPunishment;
import com.accenture.backend.enums.report.ReportStatus;
import com.accenture.backend.enums.report.ReportType;
import com.accenture.backend.service.MailSendingService;
import com.accenture.backend.service.ReportService;
import com.accenture.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ModeratorControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private UserService userService;

    @Mock
    private MailSendingService mailSendingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ModeratorController moderatorController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getReports_ShouldReturnListOfReports() {
        ReportSearchDto searchDto = new ReportSearchDto();
        List<ShortReportInfoDto> expectedReports = List.of(new ShortReportInfoDto());

        when(reportService.getReports(searchDto)).thenReturn(expectedReports);

        List<ShortReportInfoDto> actualReports = moderatorController.getReports(searchDto);

        assertNotNull(actualReports);
        assertEquals(expectedReports, actualReports);
        verify(reportService).getReports(searchDto);
    }

    @Test
    void getReportDetail_ShouldReturnReportInfo() throws IOException {
        long reportId = 1L;
        long moderatorId = 123L;
        ReportInfoDto expectedReportInfo = new ReportInfoDto();
        ReportUpdateDto reportUpdateDto = new ReportUpdateDto(reportId, moderatorId, ReportStatus.IN_REVIEW);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("moderator@example.com");
        when(userService.getUserIdByEmail("moderator@example.com")).thenReturn(moderatorId);
        when(reportService.getReportDetail(reportId)).thenReturn(expectedReportInfo);

        ReportInfoDto actualReportInfo = moderatorController.getReportDetail(reportId);

        assertNotNull(actualReportInfo);
        assertEquals(expectedReportInfo, actualReportInfo);
        verify(reportService).updateReportInfo(reportId, ReportStatus.IN_REVIEW);
        verify(reportService).getReportDetail(reportId);
    }

    @Test
    void getReportDetail_ShouldThrowException_WhenReportNotFound() {
        long invalidReportId = 999L;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("moderator@example.com");
        when(userService.getUserIdByEmail("moderator@example.com")).thenReturn(123L);
        when(reportService.getReportDetail(invalidReportId)).thenThrow(new RuntimeException("Report not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> moderatorController.getReportDetail(invalidReportId));
        assertEquals("Report not found", exception.getMessage());
    }

    @Test
    void validateReport_ShouldUpdateReportStatusAndApplyPunishment() {
        ReportValidationResultDto validationDto = new ReportValidationResultDto(1L, ReportStatus.RESOLVED, UserPunishment.BAN);
        InternalReportDto internalReport = new InternalReportDto(2L, "user@example.com", ReportType.SPAM);

        when(reportService.accusedIdAndReportReason(validationDto.getReportId())).thenReturn(internalReport);

        moderatorController.validateReport(validationDto);

        verify(reportService).updateReportInfo(validationDto.getReportId(), validationDto.getReportStatus());
        verify(userService).changeRole(2L, Role.DISABLED);
        verify(mailSendingService).sendBanNotification("user@example.com", ReportType.SPAM);
    }

    @Test
    void validateReport_ShouldNotApplyPunishment_WhenNoneSelected() {
        ReportValidationResultDto validationDto = new ReportValidationResultDto(1L, ReportStatus.RESOLVED, UserPunishment.NONE);
        InternalReportDto internalReport = new InternalReportDto(2L, "user@example.com", ReportType.SPAM);

        when(reportService.accusedIdAndReportReason(validationDto.getReportId())).thenReturn(internalReport);

        moderatorController.validateReport(validationDto);

        verify(reportService).updateReportInfo(validationDto.getReportId(), validationDto.getReportStatus());
        verifyNoInteractions(userService);
        verifyNoInteractions(mailSendingService);
    }

}
