package org.art.vertex.application.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.user.command.LoginCommand;
import org.art.vertex.application.user.command.RegisterUserCommand;
import org.art.vertex.application.user.dto.AuthenticationResponse;
import org.art.vertex.application.user.dto.UserDto;
import org.art.vertex.application.user.mapper.UserMapper;
import org.art.vertex.domain.user.exception.DuplicateEmailException;
import org.art.vertex.domain.user.security.exception.InvalidCredentialsException;
import org.art.vertex.domain.user.exception.UserNotFoundException;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.domain.user.security.PasswordEncoder;
import org.art.vertex.domain.user.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserMapper userMapper;

    public AuthenticationResponse register(RegisterUserCommand command) {
        log.debug("Registering new user. Email: {}", command.email());

        if (userRepository.existsByEmail(command.email())) {
            log.warn("Registration failed. Email already exists: {}", command.email());
            throw new DuplicateEmailException(command.email());
        }

        String encodedPassword = passwordEncoder.encode(command.password());

        User user = User.create(
            command.email(),
            encodedPassword,
            LocalDateTime.now()
        );

        user = userRepository.save(user);

        log.info("User registered successfully. User id: {}, email: {}", user.getId(), user.getEmail());

        String token = jwtTokenProvider.generateToken(user);

        UserDto userDto = userMapper.toDto(user);

        return AuthenticationResponse.builder()
            .accessToken(token)
            .tokenType("Bearer")
            .user(userDto)
            .build();
    }

    public AuthenticationResponse login(LoginCommand command) {
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
        UserDto userDto = userMapper.toDto(user);

        return AuthenticationResponse.builder()
            .accessToken(token)
            .tokenType("Bearer")
            .user(userDto)
            .build();
    }

    public UserDto getCurrentUser(String userId) {
        log.debug("Fetching current user. User id: {}", userId);

        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toDto(user);
    }
}
