package com.accenture.backend.mappper;

import com.accenture.backend.dto.user.CreateUserInfoDto;
import com.accenture.backend.dto.user.UserRoleDto;
import com.accenture.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "NOT_CONFIRMED")
    User userInfoDtoToUser(CreateUserInfoDto createUserInfoDto);

    UserRoleDto userToLoginDto(User user);

}