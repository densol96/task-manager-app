package com.accenture.backend.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    private Long id;
    private Long userId;
    private Long contactId;
    private String contactEmail;
    private String contactFirstName;
    private String contactLastName;
}
