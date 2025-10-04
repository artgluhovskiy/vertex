package org.art.vertex.infrastructure.user.security;

import org.art.vertex.domain.user.security.PasswordEncoder;

/**
 * Temporary stub implementation for password encoding.
 * TODO: Implement with BCrypt in Phase 5 (Security Implementation)
 */
public class StubPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        // TODO: Replace with BCrypt implementation
        return "STUB_ENCODED_" + rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        // TODO: Replace with BCrypt implementation
        return encodedPassword.equals("STUB_ENCODED_" + rawPassword);
    }
}
