package com.accenture.backend.controller;

import com.accenture.backend.dto.user.CreateUserInfoDto;
import com.accenture.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sign-up")
@RequiredArgsConstructor
@Tag(name = "Registration")
public class RegistrationController {

    private final UserService userService;

    @Operation(
            summary = "Register a new account",
            description = "Allows users to create a new account. Note: After registration, you will not be able to perform any actions until you verify your email. " +
                    "Use the provided email verification endpoints to complete the registration process."
    )
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserInfoDto userInfo){
        userService.createUser(userInfo);

        return ResponseEntity.ok("User created");
    }

}
