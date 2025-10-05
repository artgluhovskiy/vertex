package org.art.vertex.web.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.application.user.command.LoginCommand;
import org.art.vertex.application.user.command.RegisterUserCommand;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.web.user.dto.AuthenticationResponse;
import org.art.vertex.web.user.dto.UserDto;
import org.art.vertex.web.user.mapper.UserDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserApplicationService userApplicationService;

    private final UserDtoMapper userDtoMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterUserCommand command) {
        log.trace("Processing registration request. Email: {}", command.email());

        UserApplicationService.AuthenticationResult result = userApplicationService.register(command);

        AuthenticationResponse response = AuthenticationResponse.builder()
            .accessToken(result.getAccessToken())
            .tokenType("Bearer")
            .user(userDtoMapper.toDto(result.getUser()))
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginCommand command) {
        log.trace("Processing login request. Email: {}", command.email());

        UserApplicationService.AuthenticationResult result = userApplicationService.login(command);

        AuthenticationResponse response = AuthenticationResponse.builder()
            .accessToken(result.getAccessToken())
            .tokenType("Bearer")
            .user(userDtoMapper.toDto(result.getUser()))
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal String userId) {
        log.trace("Fetching current user. User id: {}", userId);

        User user = userApplicationService.getCurrentUser(userId);
        UserDto userDto = userDtoMapper.toDto(user);

        return ResponseEntity.ok(userDto);
    }
}
