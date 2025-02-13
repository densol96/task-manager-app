package com.accenture.backend.dto.report;

import com.accenture.backend.enums.report.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InternalReportDto {
    private long accusedId;
    private String accusedEmail;
    private ReportType reportType;
}
