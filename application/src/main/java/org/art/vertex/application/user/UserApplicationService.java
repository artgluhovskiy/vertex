package org.art.vertex.application.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.user.command.LoginCommand;
import org.art.vertex.application.user.command.RegisterUserCommand;
import org.art.vertex.application.user.model.AuthenticationResult;
import org.art.vertex.domain.user.exception.DuplicateEmailException;
import org.art.vertex.domain.user.exception.UserNotFoundException;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.shared.generator.UuidGenerator;
import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.domain.user.security.PasswordEncoder;
import org.art.vertex.domain.user.security.exception.InvalidCredentialsException;
import org.art.vertex.domain.user.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final UuidGenerator uuidGenerator;

    public AuthenticationResult register(RegisterUserCommand command) {
        log.debug("Registering new user. Email: {}", command.email());

        if (userRepository.existsByEmail(command.email())) {
            log.warn("Registration failed. Email already exists: {}", command.email());
            throw new DuplicateEmailException(command.email());
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        User user = User.create(
            uuidGenerator.generate(),
            command.email(),
            encodedPassword,
            LocalDateTime.now()
        );

        user = userRepository.save(user);

        log.info("User registered successfully. User id: {}, email: {}", user.getId(), user.getEmail());

        String token = jwtTokenProvider.generateToken(user);

        return AuthenticationResult.builder()
            .accessToken(token)
            .user(user)
            .build();
    }

    public AuthenticationResult login(LoginCommand command) {
        log.debug("User login attempt. Email: {}", command.email());

        User user = userRepository.findByEmail(command.email())
            .orElseThrow(() -> {
                log.warn("Login failed. User not found: {}", command.email());
                return new InvalidCredentialsException();
            });

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            log.warn("Login failed. Invalid password for user: {}", command.email());
            throw new InvalidCredentialsException();
        }

        log.info("User logged in successfully. User id: {}, email: {}", user.getId(), user.getEmail());

        String token = jwtTokenProvider.generateToken(user);

        return AuthenticationResult.builder()
            .accessToken(token)
            .user(user)
            .build();
    }

    public User getCurrentUser(String userId) {
        log.debug("Fetching current user. User id: {}", userId);

        return userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
