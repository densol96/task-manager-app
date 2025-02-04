package com.accenture.backend.service.userservice;

import com.accenture.backend.dto.LoginDto;
import com.accenture.backend.mappper.UserMapper;
import com.accenture.backend.model.SecurityUser;
import com.accenture.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("Loading user by email: {}", email);

        LoginDto loginDto = userRepository.findUserByEmail(email)
                .map(userMapper::toLoginDto)
                .orElseThrow(() -> {
                    log.error("User with email: {} was not found", email);
                    return new UsernameNotFoundException("Username not found");
                });

        log.info("User with email: {} is found", email);

        return new SecurityUser(loginDto);
    }
}
