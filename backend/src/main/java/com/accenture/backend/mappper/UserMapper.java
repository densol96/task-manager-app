package com.accenture.backend.mappper;

import com.accenture.backend.dto.user.UserRoleDto;
import com.accenture.backend.dto.user.UserInfoDto;
import com.accenture.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // if u have problem with this file comment the next 2 lines annotated with @Mapping,
    // run the app. then stop, uncomment this lines and run app again,
    // Idk why, but this works

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "NOT_CONFIRMED")
    User userInfoDtoToUser(UserInfoDto userInfoDto);

    UserRoleDto userToLoginDto(User user);

}