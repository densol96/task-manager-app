package com.accenture.backend.service.serviceimpl;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.accenture.backend.entity.User;
import com.accenture.backend.enums.AuthProvider;
import com.accenture.backend.enums.Role;
import com.accenture.backend.mappper.UserMapper;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.service.OAuth2Service;
import com.accenture.backend.util.SecurityUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDetails findOrCreateUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");
        String[] nameParts = fullName.split(" ");
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        Optional<User> existingUser = userRepository.findUserByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            /*
             * It is okey to allow users to login using both methods since our usual sign-up
             * process involves email verification
             * and such way is less confusig for users.
             */
            // if (user.getAuthProvider() == AuthProvider.LOCAL) {
            // throw new OAuth2Exception(
            // "This Gmail associated email has already been used to register with password.
            // Restore password if you forgot.");
            // }
            return new SecurityUser(userMapper.userToLoginDto(user));
        }

        User newUser = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(Role.USER)
                .authProvider(AuthProvider.GOOGLE)
                .build();

        return new SecurityUser(userMapper.userToLoginDto(userRepository.save(newUser)));
    }
}
