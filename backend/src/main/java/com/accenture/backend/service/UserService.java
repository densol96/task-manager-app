package com.accenture.backend.service;

import com.accenture.backend.dto.user.UserInfoDto;
import com.accenture.backend.util.SecurityUser;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

     void createUser(UserInfoDto userInfoDto);

     boolean userExists(String email);

     Long getLoggedInUserId();
}
