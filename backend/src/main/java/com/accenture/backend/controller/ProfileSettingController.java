package com.accenture.backend.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class ProfileSettingController {

    @PostMapping("/email")
    public void verifyEmail(){
        log.info("JWT token  is working");
    }

    @PostMapping("/change-password")
    public void changePassword(){
        log.error("Role access work wrong");
    }
}
