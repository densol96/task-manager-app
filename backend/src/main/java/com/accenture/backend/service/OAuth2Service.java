package com.accenture.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Service {
    UserDetails findOrCreateUser(OAuth2User ouath2User);

}
