package com.accenture.backend.dto.report;

import com.accenture.backend.dto.user.IdNameDto;

import com.accenture.backend.enums.report.ReportStatus;
import com.accenture.backend.enums.report.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportInfoDto {
    private long id;
    private IdNameDto reporter;
    private IdNameDto accused;
    private ReportType reportType;
    private List<String> evidencesUrls;
    private Timestamp createdAt;
    private ReportStatus reportStatus;
}
