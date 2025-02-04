package com.accenture.backend.dto.response;

import java.util.Map;

public record ComplexErrorDto(
        Map<String, String> errors) {
}
