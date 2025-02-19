package com.accenture.backend.mappper;

import com.accenture.backend.dto.user.CreateUserInfoDto;
import com.accenture.backend.dto.user.UserRoleDto;
import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User userInfoDtoToUser(CreateUserInfoDto userInfoDto) {
        return User.builder()
                .email(userInfoDto.getEmail())
                .firstName(userInfoDto.getFirstName())
                .lastName(userInfoDto.getLastName())
                .password(userInfoDto.getPassword())
                .role(Role.NOT_CONFIRMED)
                .build();
    }

    public UserRoleDto userToLoginDto(User user) {
        return new UserRoleDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}
