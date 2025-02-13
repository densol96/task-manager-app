package com.accenture.backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PremiumAccountDto {
    private Boolean hasActivePremiumAccount;
    private LocalDateTime expiresAt;
}
