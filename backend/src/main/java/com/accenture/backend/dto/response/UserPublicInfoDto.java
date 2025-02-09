package com.accenture.backend.dto.response;

import java.time.LocalDateTime;

import com.accenture.backend.entity.ProjectMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicInfoDto {
    private String email;
    private String firstName;
    private String lastName;
    private ProjectMember.Role projectRole;
    private LocalDateTime joinDate;
}
