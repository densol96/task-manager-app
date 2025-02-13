package com.accenture.backend.dto.report;

import com.accenture.backend.dto.user.IdNameDto;
import com.accenture.backend.enums.report.ReportStatus;
import com.accenture.backend.enums.report.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortReportInfoDto {
    private long id;
    private IdNameDto accused;
    private ReportType reportType;
    private ReportStatus reportStatus;
    private Date createdAt;
}
