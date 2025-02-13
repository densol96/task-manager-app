package com.accenture.backend.controller;

import com.accenture.backend.enums.Role;
import com.accenture.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin-dashboard")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PutMapping("/add-moderator")
    public void addModerator(@RequestParam @Valid @Email String email) {
        userService.changeRole(email, Role.MODERATOR);
    }

    @PutMapping("/disable-user")
    public void disableUser(@RequestParam @Valid @Email String email) {
        userService.changeRole(email, Role.DISABLED);
    }

}
