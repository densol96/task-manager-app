package com.accenture.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.backend.dto.response.JwtDto;
import com.accenture.backend.service.OAuth2Service;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2Service oauth2Service;

    @GetMapping("/get-jwt/{uuid}")
    public ResponseEntity<JwtDto> getMethodName(@PathVariable String uuid) {
        return new ResponseEntity<>(oauth2Service.exchangeUUIDtokenForJwt(uuid), HttpStatus.OK);
    }
}
