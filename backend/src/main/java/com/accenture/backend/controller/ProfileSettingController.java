package com.accenture.backend.controller;


import com.accenture.backend.dto.user.ChangePasswordDto;
import com.accenture.backend.enums.Role;
import com.accenture.backend.service.MailSendingService;
import com.accenture.backend.service.CodeVerificationService;
import com.accenture.backend.service.UserService;
import com.accenture.backend.util.VerificationCodeGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Profile Settings", description = "Endpoints for managing user profile settings, including email verification and password changes.")
public class ProfileSettingController {

    private final CodeVerificationService codeVerificationService;
    private final MailSendingService mailSendingService;
    private final UserService userService;

    @Operation(
            summary = "Request email verification code",
            description = "Sends a verification code to the email provided during registration. " +
                    "The email is sent via the MailSender, and the code is stored in the database for 5 minutes."
    )
    @PostMapping("/email-request")
    public void verifyEmailRequest() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String code = VerificationCodeGenerator.generateCode();

        mailSendingService.sendCode(email, code);
        codeVerificationService.storeCode(email, code);

        log.info("Verification code sent to email: {}", email);
    }

    @Operation(
            summary = "Verify email code",
            description = "Validates the verification code. If the code matches the one stored in Redis, the user's role is updated from NOT_CONFIRMED to USER."
    )
    @PostMapping("/email-code")
    public void verifyEmailCode(@RequestParam String code) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isVerified = codeVerificationService.verifyCode(email, code);

        if (isVerified) {
            log.info("Email {} verified successfully.", email);
            userService.changeRole(email, Role.USER);
        } else {
            log.error("Verification failed for email {}: code does not match.", email);
        }
    }

    @Operation(
            summary = "Change password",
            description = "Allows a user to change their password. Note: You need the USER role to access this endpoint. " +
                    "to det USER role verify your email using /api/v1/user/email-request and /api/v1/user/email-code."
    )
    @PutMapping("/change-password")
    public void changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto);
        log.info("Password changed successfully for user.");
    }
}