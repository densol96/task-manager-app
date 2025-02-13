package com.accenture.backend.controller;


import com.accenture.backend.dto.user.ChangePasswordDto;
import com.accenture.backend.enums.Role;
import com.accenture.backend.service.MailSendingService;
import com.accenture.backend.service.CodeVerificationService;
import com.accenture.backend.service.UserService;
import com.accenture.backend.util.VerificationCodeGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class ProfileSettingController {

    private final CodeVerificationService codeVerificationService;
    private final MailSendingService mailSendingService;
    private final UserService userService;

    @PostMapping("/email-request")
    public void verifyEmailRequest(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String code = VerificationCodeGenerator.generateCode();

        mailSendingService.sendCode(email, code);
        codeVerificationService.storeCode(email, code);
    }

    @PostMapping("/email-code")
    public void verifyEmailCode(@RequestParam String code){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isVerified = codeVerificationService.verifyCode(email, code);

        if (isVerified){
            log.info("email {}, was approved - {}", email, isVerified);
            userService.changeRole(email, Role.USER);
        } else {
            log.error("code for verifying email: {} does not match to sent", email);
        }
    }

    @PutMapping("/change-password")
    public void changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto){
        userService.changePassword(changePasswordDto);
    }
}
