package com.accenture.backend.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.accenture.backend.dto.response.JwtDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface OAuth2Service {
    void handleOAuth2Success(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException;

    JwtDto exchangeUUIDtokenForJwt(String uuid);
}
