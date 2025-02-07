package com.accenture.backend.service.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.accenture.backend.dto.user.UserInfoDto;
import com.accenture.backend.dto.user.UserRoleDto;
import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;
import com.accenture.backend.exception.EmailAlreadyInUseException;
import com.accenture.backend.mappper.UserMapper;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.util.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_userFound() {
        String email = "test@example.com";

        User user = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("securePass123")
                .role(Role.USER)
                .build();

        UserRoleDto userRoleDto = new UserRoleDto("john.doe@example.com",
                "securePass123", Role.USER);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.userToLoginDto(user)).thenReturn(userRoleDto);

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(userRoleDto, ((SecurityUser) userDetails).getUserRoleDto());
        verify(userRepository, times(1)).findUserByEmail(email);
        verify(userMapper, times(1)).userToLoginDto(user);
    }

    @Test
    void loadUserByUsername_userNotFound() {
        String email = "test@example.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
        verify(userRepository, times(1)).findUserByEmail(email);
        verify(userMapper, times(0)).userToLoginDto(any());
    }

    @Test
    void createUser_userAlreadyExists() {
        String email = "test@example.com";
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setEmail(email);

        when(userRepository.countUserByEmail(email)).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(userInfoDto));
        verify(userRepository, times(1)).countUserByEmail(email);
        verify(userRepository, times(0)).save(any(User.class));
    }


    @Test
    void createUser_newUser() {
        String email = "test@example.com";
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("securePass123")
                .build();

        when(userRepository.countUserByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("encodedPassword");
        when(userMapper.userInfoDtoToUser(any(UserInfoDto.class))).thenReturn(new User());

        userService.createUser(userInfoDto);

        verify(userRepository, times(1)).countUserByEmail(email);
        verify(passwordEncoder, times(1)).encode("securePass123");
        verify(userMapper, times(1)).userInfoDtoToUser(userInfoDto);
        verify(userRepository, times(1)).save(any(User.class));

        assertEquals("encodedPassword", passwordEncoder.encode(userInfoDto.getPassword()));
    }

    @Test
    void userExists_userExists() {
        String email = "test@example.com";

        when(userRepository.countUserByEmail(email)).thenReturn(true);

        assertTrue(userService.userExists(email));
        verify(userRepository, times(1)).countUserByEmail(email);
    }

    @Test
    void userExists_userDoesNotExist() {
        String email = "test@example.com";

        when(userRepository.countUserByEmail(email)).thenReturn(false);

        assertFalse(userService.userExists(email));
        verify(userRepository, times(1)).countUserByEmail(email);
    }
}
