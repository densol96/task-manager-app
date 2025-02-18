package com.accenture.backend.service;

import com.accenture.backend.dto.response.JwtDto;

public interface OAuth2Sevice {
    JwtDto exchangeOAuthTokenForJWT(String oauthToken);
}
