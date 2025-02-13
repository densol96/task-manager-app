package com.accenture.backend.controller;

import com.accenture.backend.dto.report.InternalReportDto;
import com.accenture.backend.dto.report.ReportInfoDto;
import com.accenture.backend.dto.report.ReportSearchDto;
import com.accenture.backend.dto.report.ReportUpdateDto;
import com.accenture.backend.dto.report.ReportValidationResultDto;
import com.accenture.backend.dto.report.ShortReportInfoDto;
import com.accenture.backend.enums.Role;
import com.accenture.backend.enums.report.ReportStatus;
import com.accenture.backend.service.MailSendingService;
import com.accenture.backend.service.ReportService;
import com.accenture.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Transactional
@RequiredArgsConstructor
@RequestMapping("/api/v1/moderator-dashboard")
public class ModeratorController {

    private final ReportService reportService;
    private final UserService userService;
    private final MailSendingService mailSendingService;
    private final SimpMessagingTemplate messagingTemplate;

    // using this method u can get report for certain user, or specific report type, or specific report status ext
    @GetMapping
    public List<ShortReportInfoDto> getReports(@RequestBody ReportSearchDto reportSearchDto) {
        return reportService.getReports(reportSearchDto);
    }

    @GetMapping("/{reportId}")
    public ReportInfoDto getReportDetail(@PathVariable long reportId) {
        log.info("getting details about moderator who reviewing report {}", reportId);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        long moderatorId = userService.getUserIdByEmail(email);

        log.info("sending info throw websocket that moderator {} is reviewing report {}", moderatorId, reportId);
        ReportUpdateDto reportUpdateDto = new ReportUpdateDto(reportId, moderatorId, ReportStatus.IN_REVIEW);
        messagingTemplate.convertAndSend("/topic/reports", reportUpdateDto);

        log.info("updating report status to IN_REVIEW in database for report {}", reportId);
        reportService.updateReportInfo(reportId, ReportStatus.IN_REVIEW);

        return reportService.getReportDetail(reportId);
    }


    @PutMapping("/validate")
    public void validateReport(@Valid @RequestBody ReportValidationResultDto dto) {
        log.info("report {} is validated, updating it status to {} in database", dto.getReportId(), dto.getReportStatus());
        reportService.updateReportInfo(dto.getReportId(), dto.getReportStatus());

        InternalReportDto internalReportDto = reportService.accusedIdAndReportReason(dto.getReportId());

        log.info("checking punishment for user {}, based on report {}", internalReportDto.getAccusedId(), dto.getReportId());
        switch (dto.getUserPunishment()) {
            case BAN:
                log.info("user {} was banned", internalReportDto.getAccusedId());
                userService.changeRole(internalReportDto.getAccusedId(), Role.DISABLED);
                mailSendingService.sendBanNotification(internalReportDto.getAccusedEmail(), internalReportDto.getReportType());
                break;
            case WARNING:
                log.info("user {} was warned", internalReportDto.getAccusedId());

                // request to notification service

                break;
            case NONE:
                log.info("user {} did not need to be punished", internalReportDto.getAccusedId());
                break;
            default:
                log.info("moderator did not validate report {}, so punishment for user {} is not defined", dto.getReportId(), internalReportDto.getAccusedId());
                break;
        }
    }

}
