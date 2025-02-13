package com.accenture.backend.service;

import com.accenture.backend.dto.report.InternalReportDto;
import com.accenture.backend.dto.report.ReportInfoDto;
import com.accenture.backend.dto.report.ReportSearchDto;
import com.accenture.backend.dto.report.ShortReportInfoDto;
import com.accenture.backend.entity.Evidence;
import com.accenture.backend.enums.report.ReportStatus;
import com.accenture.backend.enums.report.ReportType;

import java.util.List;

public interface ReportService {

    void createReport(long reporterId, long accusedId, ReportType reportType, List<Evidence> evidences);

    List<ShortReportInfoDto> getReports(ReportSearchDto reportSearchDto);

    ReportInfoDto getReportDetail(long reportId);

    InternalReportDto accusedIdAndReportReason(long reportId);

    void updateReportInfo(long reportId, ReportStatus reportStatus);
}
