package com.accenture.backend.service.verificationservice;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class EmailVerificationServiceImpl{

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String toEmail){
        String code = generateCode();
        String processHtml = processHtml(code);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            log.info("sending verification code for email: {}", toEmail);

            helper.setText(processHtml, true);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Our Service!");
            helper.setFrom("noreply@yourdomain.com");
            mailSender.send(mimeMessage);

            log.info("verification email was successfully sent to email {}:", toEmail);
        } catch (MessagingException e) {
            log.error("Exception occurred while sending verification email to {}. Exception details: {}", toEmail, e.getMessage());
        }
    }

    private String processHtml(String code){
        String[] codeArray = code.split("");
        Context context = new Context();
        context.setVariable("code", codeArray);
        return templateEngine.process("templateHtml", context);
    }

    private static String generateCode(){
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

}
