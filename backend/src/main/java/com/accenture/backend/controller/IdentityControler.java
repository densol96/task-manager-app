package com.accenture.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.backend.dto.response.UserContextDto;
import com.accenture.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/identity")
@RequiredArgsConstructor
public class IdentityControler {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserContextDto> getMethodName() {
        return ResponseEntity.ok(userService.getIdentity());
    }

}
