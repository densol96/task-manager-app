package com.accenture.backend.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BasicNestedResponseDto<T> {
    private String message;
    private T data;
}