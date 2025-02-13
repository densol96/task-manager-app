package com.accenture.backend.service;

import com.accenture.backend.dto.user.ChangePasswordDto;
import com.accenture.backend.dto.user.CreateUserInfoDto;
import com.accenture.backend.enums.Role;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends UserDetailsService {

     void createUser(CreateUserInfoDto createUserInfoDto);

     @Transactional
     void saveUser(CreateUserInfoDto createUserInfoDto);

     boolean userExists(String email);

     void changeRole(String email, Role role);

     void changeRole(long userId, Role role);

     void changePassword(ChangePasswordDto changePasswordDto);

     long getUserIdByEmail(String email);
}
