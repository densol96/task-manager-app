package com.accenture.backend.mappper;

import com.accenture.backend.dto.LoginDto;
import com.accenture.backend.dto.UserInfoDto;
import com.accenture.backend.entity.User;
import com.accenture.backend.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

//    LoginDto toLoginDto(User entity);
//
//    //@Mapping(target = "role", expression = "java(com.accenture.backend.model.Role.NOT_CONFIRMED)")
//    User mainInfoToUser(UserInfoDto userInfoDto);

}