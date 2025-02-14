package com.accenture.backend.dto.task;

import com.accenture.backend.entity.MemberTaskAssignment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberTaskAssignmentDto {
    private Long id;
    private Long memberId;
    private Long taskId;
    private LocalDateTime assignedOn;
    private String email;

    public static MemberTaskAssignmentDto fromEntity(MemberTaskAssignment assignment) {
        return new MemberTaskAssignmentDto(
                assignment.getId(),
                assignment.getMember().getId(),
                assignment.getTask().getId(),
                assignment.getAssignedOn(),
                assignment.getMember().getUser().getEmail());
    }

    public MemberTaskAssignment toEntity() {
        MemberTaskAssignment assignment = new MemberTaskAssignment();
        assignment.setId(this.id);
        assignment.setAssignedOn(this.assignedOn != null ? this.assignedOn : LocalDateTime.now());
        return assignment;
    }
}
