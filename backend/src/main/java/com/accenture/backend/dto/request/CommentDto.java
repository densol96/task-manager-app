package com.accenture.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;
}
