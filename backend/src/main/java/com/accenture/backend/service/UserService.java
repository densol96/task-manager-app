package com.accenture.backend.service;

import com.accenture.backend.dto.user.ChangePasswordDto;
import com.accenture.backend.dto.user.UserInfoDto;
import com.accenture.backend.enums.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

     void createUser(UserInfoDto userInfoDto);

     boolean userExists(String email);

     void changeRole(String email, Role role);

     void changePassword(ChangePasswordDto changePasswordDto);

     Long getLoggedInUserId();
}
