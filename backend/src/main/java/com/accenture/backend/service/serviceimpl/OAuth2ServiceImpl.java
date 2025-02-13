package com.accenture.backend.service.serviceimpl;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import com.accenture.backend.dto.response.JwtDto;
import com.accenture.backend.entity.User;
import com.accenture.backend.enums.AuthProvider;
import com.accenture.backend.enums.Role;
import com.accenture.backend.mappper.UserMapper;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.service.JwtService;
import com.accenture.backend.service.OAuth2Service;
import com.accenture.backend.service.TokenStoreService;
import com.accenture.backend.util.SecurityUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final TokenStoreService tokenStore;

    @Value("${frontend.domain.url}")
    private String frontEndUrl;

    private UserDetails findOrCreateUser(OAuth2User oAuth2User) {
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
            return new SecurityUser(userMapper.userToLoginDto(user));

            // if (user.getAuthProvider() == AuthProvider.LOCAL) {
            // throw new OAuth2Exception(
            // "This Gmail associated email has already been used to register with password.
            // Restore password if you forgot.");
            // }
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

    @Override
    public void handleOAuth2Success(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        UserDetails userDetails = findOrCreateUser(oAuth2User);
        String uuid = UUID.randomUUID().toString();
        tokenStore.addToken(uuid, jwtService.generateToken(userDetails));
        String redirectUrl = frontEndUrl + "/oauth2-redirect?uuid=" + uuid;
        response.sendRedirect(redirectUrl);
    }

    @Override
    public JwtDto exchangeUUIDtokenForJwt(String uuid) {
        return new JwtDto(tokenStore.getToken(uuid));
    }
}
