package com.accenture.backend.controller;

import com.accenture.backend.dto.UserInfoDto;
import com.accenture.backend.entity.User;
import com.accenture.backend.service.userservice.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sing-up")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

//    @PostMapping("/email")
//    public void verifyEmail(){
//
//        userService
//    }

    @PostMapping("/user-info")
    public void createUser(@Valid UserInfoDto userInfo){

    }


}
