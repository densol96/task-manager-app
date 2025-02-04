package com.accenture.backend.mappper;

import com.accenture.backend.dto.LoginDto;
import com.accenture.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    LoginDto toLoginDto(User entity);
    User toEntity(LoginDto loginDto);
}