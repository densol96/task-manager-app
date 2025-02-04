package com.accenture.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class User {
    @Id
    Long id;
    String firstName;
    String lastName;
    String email;
}
