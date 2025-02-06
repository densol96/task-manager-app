package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.dto.user.UserRoleDto;
import com.accenture.backend.dto.user.UserInfoDto;
import com.accenture.backend.entity.User;
import com.accenture.backend.exception.custom.EmailAlreadyInUseException;
import com.accenture.backend.mappper.UserMapper;
import com.accenture.backend.util.SecurityUser;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("Loading user by email: {}", email);

        UserRoleDto userRoleDto = userRepository.findUserByEmail(email)
                .map(userMapper::userToLoginDto)
                .orElseThrow(() -> {
                    log.error("User with email: {} was not found", email);
                    return new UsernameNotFoundException("Username not found");
                });

        log.info("User with email: {} is found", email);

        return new SecurityUser(userRoleDto);
    }

    @Override
    public void createUser(UserInfoDto userInfoDto){
        if (userExists(userInfoDto.getEmail())) {
            log.error("Email {} is already taken", userInfoDto.getEmail());
            throw new EmailAlreadyInUseException("This email already taken");
        }

        saveUser(userInfoDto);
    }

    private void saveUser(UserInfoDto userInfoDto) {
        log.info("Creating user with email: {}", userInfoDto.getEmail());
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        User user = userMapper.userInfoDtoToUser(userInfoDto);

        log.info("Saving user with email: {} to database", user.getEmail());
        userRepository.save(user);
    }

    @Override
    public boolean userExists(String email){
        log.info("checking existence of the user with email: {}", email);
        return userRepository.countUserByEmail(email);
    }
}
