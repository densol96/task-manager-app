package com.accenture.backend.dto.report;

import com.accenture.backend.enums.UserPunishment;
import com.accenture.backend.enums.report.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportValidationResultDto {
    @NonNull
    private Long reportId;
    @NonNull
    private ReportStatus reportStatus;
    @NonNull
    private UserPunishment userPunishment;
}
