package org.art.vertex.web.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.user.security.PasswordEncoder;

/**
 * Production-ready password encoder using BCrypt hashing algorithm.
 * BCrypt is specifically designed for password hashing with built-in salt generation
 * and configurable computational cost to resist brute-force attacks.
 * <p>
 * This implementation wraps Spring Security's BCryptPasswordEncoder for production use.
 */
@Slf4j
@RequiredArgsConstructor
public final class BCryptPasswordEncoder implements PasswordEncoder {

    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder delegate;

    @Override
    public String encode(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Raw password cannot be null");
        }

        if (rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password cannot be empty");
        }

        if (rawPassword.length() > 72) {
            log.warn("Password length exceeds BCrypt's 72 character limit and will be truncated");
        }

        String hashed = delegate.encode(rawPassword);
        log.trace("Password encoded successfully");
        return hashed;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            log.debug("Raw password is null, authentication failed");
            return false;
        }

        if (encodedPassword == null || encodedPassword.isEmpty()) {
            log.debug("Encoded password is null or empty, authentication failed");
            return false;
        }

        try {
            boolean result = delegate.matches(rawPassword, encodedPassword);
            log.trace("Password match result: {}", result);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Failed to verify password: {}", e.getMessage());
            return false;
        }
    }
}
