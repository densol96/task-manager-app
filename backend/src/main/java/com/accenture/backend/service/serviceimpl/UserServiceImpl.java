package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.dto.user.ChangePasswordDto;
import com.accenture.backend.dto.user.CreateUserInfoDto;
import com.accenture.backend.dto.user.UserRoleDto;
import com.accenture.backend.entity.User;
import com.accenture.backend.enums.Role;
import com.accenture.backend.exception.EmailAlreadyInUseException;
import com.accenture.backend.mappper.UserMapper;
import com.accenture.backend.util.SecurityUser;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
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
    public void createUser(CreateUserInfoDto createUserInfoDto){
        if (userExists(createUserInfoDto.getEmail())) {
            log.error("Email {} is already taken", createUserInfoDto.getEmail());
            throw new EmailAlreadyInUseException("This email already taken");
        }

        saveUser(createUserInfoDto);
    }

    @Transactional
    @Override
    public void saveUser(CreateUserInfoDto createUserInfoDto) {
        log.info("Creating user with email: {}", createUserInfoDto.getEmail());
        createUserInfoDto.setPassword(passwordEncoder.encode(createUserInfoDto.getPassword()));
        User user = userMapper.userInfoDtoToUser(createUserInfoDto);

        log.info("Saving user with email: {} to database", user.getEmail());
        userRepository.save(user);
    }

    @Override
    public boolean userExists(String email){
        log.info("checking existence of the user with email: {}", email);
        return userRepository.countUserByEmail(email);
    }

    @Transactional
    @Override
    public void changeRole(String email, Role role) {
        log.info("changing role for user: {}, now his role is: {}", email, role);

        User user = userRepository.findUserByEmail(email).orElseThrow(() -> {
            log.error("User with email: {} was not found", email);
            return new UsernameNotFoundException("Username not found");
        });

        user.setRole(role);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void changeRole(long userId, Role role) {
        log.info("changing role for user with id: {}, now his role is: {}", userId, role);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id: {} was not found", userId);
            return new UsernameNotFoundException("Username not found");
        });

        user.setRole(role);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findUserByEmail(email).orElseThrow(() -> {
            log.error("User with email: {} was not found", email);
            return new UsernameNotFoundException("Username not found");
        });

        if (passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            log.info("Password for user {} is changed", email);
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
        } else {
            log.error("Attempt of password changing for user with email: {} failed - bad credentials", email);
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    public long getUserIdByEmail(String email) {
        log.info("Getting user id by email: {}", email);
         return userRepository.findUserByEmail(email).orElseThrow(() -> {
            log.error("User with email: {} was not found", email);
            return new UsernameNotFoundException("Username not found");
        }).getId();
    }


}
