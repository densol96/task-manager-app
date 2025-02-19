package com.accenture.backend.dto.task;

import com.accenture.backend.entity.MemberTaskAssignment;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberTaskAssignmentDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime assignedOn;

    public static MemberTaskAssignmentDto fromEntity(MemberTaskAssignment assignment) {
        return new MemberTaskAssignmentDto(
                assignment.getId(),
                assignment.getMember().getId(),
                assignment.getTask().getId(),
                assignment.getAssignedOn()
        );
    }

    public MemberTaskAssignment toEntity() {
        MemberTaskAssignment assignment = new MemberTaskAssignment();
        assignment.setId(this.id);
        assignment.setAssignedOn(this.assignedOn != null ? this.assignedOn : LocalDateTime.now());
        return assignment;
    }
}
