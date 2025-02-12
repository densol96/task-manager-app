package com.accenture.backend.repository;

import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class UserRepositoryTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @Autowired UserRepository userRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    void setUp(){
        User user1 = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("securePass123")
                .role(Role.USER)
                .build();

        userRepository.save(user1);

        User user2 = User.builder()
                .email("jane.smith@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("anotherSecurePass")
                .role(Role.ADMIN)
                .build();

        userRepository.save(user2);

        User user3 = User.builder()
                .email("bob.miller@example.com")
                .firstName("Bob")
                .lastName("Miller")
                .password("bobSecurePass")
                .role(Role.USER)
                .build();

        userRepository.save(user3);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findUserByEmail_Exists() {
        Optional<User> userOptional = userRepository.findUserByEmail("john.doe@example.com");

        assertTrue(userOptional.isPresent(), "User should be found");
        assertEquals("john.doe@example.com", userOptional.get().getEmail());
    }

    @Test
    void findUserByEmail_NotExists() {
        Optional<User> userOptional = userRepository.findUserByEmail("notfound@example.com");

        assertFalse(userOptional.isPresent(), "User should not be found");
    }

}