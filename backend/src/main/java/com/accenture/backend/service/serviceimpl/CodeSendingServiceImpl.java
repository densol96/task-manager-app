package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.service.CodeSendingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Slf4j
@Service
@RequiredArgsConstructor
public class CodeSendingServiceImpl implements CodeSendingService {

    @Value("${application.name}")
    private String company;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String toEmail, String code){

        Context context = new Context();
        context.setVariable("approvalCode", code);
        context.setVariable("companyName", company);
        String html = templateEngine.process("templateHtml", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            log.info("sending verification code for email: {}", toEmail);

            helper.setText(html, true);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Our Service!");
            mailSender.send(mimeMessage);

            log.info("verification email was successfully sent to email {}:", toEmail);
        } catch (MessagingException e) {
            log.error("Exception occurred while sending verification email to {}. Exception details: {}", toEmail, e.getMessage());
        }
    }
}
