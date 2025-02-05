package com.accenture.backend.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ValidationErrorResponseDto {
    private Map<String, String> errors;
}