package com.accenture.backend.service;

import com.accenture.backend.enums.report.ReportType;

public interface MailSendingService {
    void sendCode(String toEmail, String code);

    void sendBanNotification(String toEmail, ReportType reportType);
}
