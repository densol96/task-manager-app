package com.accenture.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcceptProjectDto {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Visibility status is required")
    private Boolean isPublic;

    @NotNull(message = "Maximum participants cannot be null")
    @Min(value = 1, message = "At least one participant is required")
    @Max(value = 20, message = "Maximum participants limit is 20")
    private Integer maxParticipants;
}
