package com.accenture.backend.dto.report;

import com.accenture.backend.enums.report.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportUpdateDto {
    private Long reportId;
    private long moderatorId;
    private ReportStatus reportStatus;
}
