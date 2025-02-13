package com.accenture.backend.controller;

import com.accenture.backend.dto.user.LoginDto;
import com.accenture.backend.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
@Tag(name = "Login", description = "Accessibility: All users")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Operation(
            summary = "JWT Token Generation",
            description = "Default login endpoint. Enter your email and password to receive a JWT token."
    )
    @PostMapping
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {

        System.out.println(loginDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println(loginDto.getPassword());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }
}
