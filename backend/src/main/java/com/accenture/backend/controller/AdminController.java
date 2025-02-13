package com.accenture.backend.controller;

import com.accenture.backend.enums.Role;
import com.accenture.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Dashboard", description = "Accessibility: Admin role required")
public class AdminController {

    private final UserService userService;

    @Operation(
            summary = "Add Moderator",
            description = "Promotes an existing user to the role of moderator. " +
                    "Note that you cannot create a moderator from an email that does not exist in the system. " +
                    "Once promoted, the user will no longer assess project and tasks. " +
                    "Instead, they will review reports and make decisions based on them."
    )
    @PutMapping("/add-moderator")
    public void addModerator(@RequestParam @Valid @Email String email) {
        userService.changeRole(email, Role.MODERATOR);
    }

    @Operation(
            summary = "Disable User",
            description = "Disables the user account. " +
                    "The user will no longer be able to log in to the system. " +
                    "This action is irreversible."
    )
    @PutMapping("/disable-user")
    public void disableUser(@RequestParam @Valid @Email String email) {
        userService.changeRole(email, Role.DISABLED);
    }

}
