package com.accenture.backend.dto.response;

import com.accenture.backend.entity.ProjectMember;

public record OwnerShortDto(
                Long userId,
                Long memberId,
                String firstName,
                String lastName,
                String email) {

        public static OwnerShortDto fromEntity(ProjectMember projectMember) {
                var asUser = projectMember.getUser();
                return new OwnerShortDto(
                                asUser.getId(),
                                projectMember.getId(),
                                asUser.getFirstName(),
                                asUser.getLastName(),
                                asUser.getEmail());
        }
}
