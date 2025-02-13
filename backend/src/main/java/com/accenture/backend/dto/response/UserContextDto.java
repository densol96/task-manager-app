package com.accenture.backend.dto.response;

import com.accenture.backend.dto.user.UserRoleDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserContextDto {
    private UserRoleDto user;
    private PremiumAccountDto premiumAccount;
}
