package org.art.vertex.web.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.application.user.command.LoginCommand;
import org.art.vertex.application.user.command.RegisterUserCommand;
import org.art.vertex.application.user.dto.AuthenticationResponse;
import org.art.vertex.application.user.dto.UserDto;
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

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterUserCommand command) {
        log.trace("Processing registration request. Email: {}", command.email());
        AuthenticationResponse response = userApplicationService.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginCommand command) {
        log.trace("Processing login request. Email: {}", command.email());
        AuthenticationResponse response = userApplicationService.login(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal String userId) {
        log.trace("Fetching current user. User id: {}", userId);
        UserDto user = userApplicationService.getCurrentUser(userId);
        return ResponseEntity.ok(user);
    }
}
