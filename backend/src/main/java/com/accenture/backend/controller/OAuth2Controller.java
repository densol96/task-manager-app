package com.accenture.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.accenture.backend.dto.response.JwtDto;
import com.accenture.backend.service.OAuth2Sevice;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2Sevice service;

    @PostMapping("/login/oauth2/code/google")
    public ResponseEntity<JwtDto> exchangeOAuthToken(@RequestBody Map<String, String> body) {
        String oauthToken = body.get("oauthToken");
        System.out.println(oauthToken);
        return new ResponseEntity<>(service.exchangeOAuthTokenForJWT(oauthToken), HttpStatus.OK);
    }

}
