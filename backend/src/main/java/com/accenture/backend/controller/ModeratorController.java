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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@Tag(name = "Moderator Dashboard", description = "Accessibility: Moderator role required")
public class ModeratorController {

    private final ReportService reportService;
    private final UserService userService;
    private final MailSendingService mailSendingService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(
            summary = "Get Reports for Moderator",
            description = "Retrieve a list of reports that match the specified criteria, with support for sorting, pagination, and filtering. " +
                    "The response includes a list of reports containing the following details:\n\n" +
                    "- **id**: Report ID\n" +
                    "- **accused**: An object containing the ID and name of the accused (`IdNameDto`)\n" +
                    "- **reportType**: The type of the report (`ReportType`)\n" +
                    "- **reportStatus**: The current status of the report (`ReportStatus`)\n" +
                    "- **createdAt**: The date when the report was created\n\n" +
                    "If you need detailed information about a specific report, you can access it through `/api/v1/moderator-dashboard/{reportId}`. " +
                    "Default settings will be applied if certain parameters are not provided in the request. For more information about default settings, refer to the `ReportSearchHelper` class."
    )
    @PostMapping
    public List<ShortReportInfoDto> getReports(@RequestBody ReportSearchDto reportSearchDto) {
        return reportService.getReports(reportSearchDto);
    }


    @Operation(
            summary = "Get Full Report Details for a Specific Report",
            description = "Retrieve detailed information about a specific report. This endpoint is designed for moderators during the review process. " +
                    "When a moderator accesses a report through this endpoint, the following actions will occur:\n\n" +
                    "1. The report status will be updated to `IN_REVIEW` in the database.\n" +
                    "2. A notification will be sent through the WebSocket at `ws/topic/reports`.\n\n" +
                    "The WebSocket (`ws/topic/reports`) is protected by Spring Security and is only accessible to users with the 'MODERATOR' role. " +
                    "This ensures secure communication of updates to all connected moderators.\n\n" +
                    "If the report is currently displayed in the list of available reports (retrieved via the `/api/v1/moderator-dashboard` endpoint), " +
                    "the front-end should either:\n\n" +
                    "- Remove it from the view, or\n" +
                    "- Make a request to the database to update the list of available reports.\n\n" +
                    "The exact behavior depends on the front-end implementation."
    )
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

    @Operation(
            summary = "Moderator Decision Handling for a Report",
            description = "This endpoint is called from the front end, even if the moderator does not make a decision but only views the report details and exits. " +
                    "to make the report visible again to other moderators. This endpoint interacts with services to save updated info about report to database and if needed execute appropriate actions, such as: " +
                    "- Banning the account and sending an email notification to the user, or " +
                    "- Issuing a warning through the local system."

    )
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
