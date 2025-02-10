package com.accenture.backend.initialization;

import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;
import com.accenture.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String[] args) {
        if (userRepository.findUserByEmail("user@test.com").isEmpty()) {
            User defaultUser = User.builder()
                    .email("user@test.com")
                    .firstName("Bob")
                    .lastName("Miller")
                    .password(passwordEncoder.encode("11111111"))
                    .role(Role.USER)
                    .build();

            userRepository.save(defaultUser);
            log.info("Default user created: user@test.com / 11111111");
        } else {
            log.info("Default user already exists!");
        }
    }
}
