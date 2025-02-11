package com.accenture.backend.dto.user;

import com.accenture.backend.enums.MessagePrivacy;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagePrivacyDto {
    private MessagePrivacy messagePrivacy;
}
