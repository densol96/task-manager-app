package com.accenture.backend.initialization;

import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;
import com.accenture.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.account.email}")
    private String adminEmail;

    @Value("${admin.account.password}")
    private String adminPassword;

    @Value("${not-confirmed.account.email}")
    private String notConfirmedEmail;

    @Value("${not-confirmed.account.password}")
    private String notConfirmedPassword;

    @Value("${user.account.email}")
    private String userEmail;

    @Value("${user.account1.email}")
    private String userEmail1;
    @Value("${user.account2.email}")
    private String userEmail2;
    @Value("${user.account3.email}")
    private String userEmail3;
    @Value("${user.account4.email}")
    private String userEmail4;
    @Value("${user.account5.email}")
    private String userEmail5;
    @Value("${user.account6.email}")
    private String userEmail6;
    @Value("${user.account7.email}")
    private String userEmail7;

    @Value("${user.account.password}")
    private String userPassword;

    @Value("${moderator.account.email}")
    private String moderatorEmail;

    @Value("${moderator.account.password}")
    private String moderatorPassword;


    @Override
    public void run(String[] args) {
        createUserIfNotExists(adminEmail, adminPassword, "Alice", "Johnson", Role.ADMIN);
        createUserIfNotExists(moderatorEmail, moderatorPassword, "Eve", "Williams", Role.MODERATOR);
        createUserIfNotExists(userEmail, userPassword, "Bob", "Miller", Role.USER);
        createUserIfNotExists(userEmail1, userPassword, "Alice", "Smith", Role.USER);
        createUserIfNotExists(userEmail2, userPassword, "John", "Doe", Role.USER);
        createUserIfNotExists(userEmail3, userPassword, "Emily", "Clark", Role.USER);
        createUserIfNotExists(userEmail4, userPassword, "Michael", "Brown", Role.USER);
        createUserIfNotExists(userEmail5, userPassword, "Jessica", "Taylor", Role.USER);
        createUserIfNotExists(userEmail6, userPassword, "David", "Wilson", Role.USER);
        createUserIfNotExists(userEmail7, userPassword, "Sophia", "Anderson", Role.USER);
        createUserIfNotExists(notConfirmedEmail, notConfirmedPassword, "Charlie", "Smith", Role.NOT_CONFIRMED);
    }

    private void createUserIfNotExists(String email, String password, String firstName, String lastName, Role role) {
        if (userRepository.findUserByEmail(email).isEmpty()) {
            User user = User.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build();

            userRepository.save(user);
            log.info("User created: {} / {} (Role: {})", email, password, role);
        } else {
            log.info("User already exists: {}", email);
        }
    }
}
