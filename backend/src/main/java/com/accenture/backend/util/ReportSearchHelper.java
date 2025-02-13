package com.accenture.backend.util;

import com.accenture.backend.dto.report.ReportSearchDto;
import com.accenture.backend.enums.report.ReportStatus;

public class ReportSearchHelper {
    public static void applyDefaults(ReportSearchDto reportSearchDto) {
        if (reportSearchDto.getPage() == null) {
            reportSearchDto.setPage(0);
        }
        if (reportSearchDto.getSize() == null) {
            reportSearchDto.setSize(10);
        }
        if (reportSearchDto.getSortBy() == null) {
            reportSearchDto.setSortBy("createdAt");

            if (reportSearchDto.getSortOrder() == null) {
                reportSearchDto.setSortOrder("asc");
            }
        }
        if (reportSearchDto.getSortOrder() == null) {
            reportSearchDto.setSortOrder("desc");
        }
        if (reportSearchDto.getReportStatus() == null) {
            reportSearchDto.setReportStatus(ReportStatus.OPEN);
        }

    }
}
