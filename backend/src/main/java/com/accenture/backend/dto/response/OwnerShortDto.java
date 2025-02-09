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
                                .userId(asUser.getId())
                                .memberId(projectMember.getId())
                                .firstName(asUser.getFirstName())
                                .lastName(asUser.getLastName())
                                .email(asUser.getEmail()).build();
        }
}