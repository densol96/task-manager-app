package com.accenture.backend.dto.response;

import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.entity.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OwnerShortDto {

        private Long userId;
        private Long memberId;
        private String firstName;
        private String lastName;
        private String email;

        public static OwnerShortDto fromEntity(ProjectMember projectMember) {
                User asUser = projectMember.getUser();

                return OwnerShortDto.builder()
                                .userId(asUser == null ? null : asUser.getId())
                                .memberId(projectMember.getId())
                                .firstName(asUser == null ? null : asUser.getFirstName())
                                .lastName(asUser == null ? null : asUser.getLastName())
                                .email(asUser == null ? null : asUser.getEmail()).build();
        }
}