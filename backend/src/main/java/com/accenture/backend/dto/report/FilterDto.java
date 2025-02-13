package com.accenture.backend.dto.report;

import com.accenture.backend.enums.report.ReportStatus;
import com.accenture.backend.enums.report.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FilterDto {
    private Long reporterId;
    private Long accusedId;
    private ReportType reportType;
    private ReportStatus reportStatus;
}
