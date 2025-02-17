package com.accenture.backend.controller;

import com.accenture.backend.dto.user.UserInfoDto;
import com.accenture.backend.service.UserService;
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
public class RegistrationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserInfoDto userInfo) {
        userService.createUser(userInfo);

        return ResponseEntity.ok("User created. Make sure to verify the email so you can see and create projects.");
    }

}
