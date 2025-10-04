package org.art.vertex.infrastructure.user.security;

import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.user.security.JwtTokenProvider;

import java.util.UUID;

/**
 * Temporary stub implementation for JWT token handling.
 * TODO: Implement with actual JWT library in Phase 5 (Security Implementation)
 */
public class StubJwtTokenProvider implements JwtTokenProvider {

    @Override
    public String generateToken(User user) {
        // TODO: Replace with actual JWT generation
        return "STUB_TOKEN_" + user.getId();
    }

    @Override
    public String extractUserId(String token) {
        // TODO: Replace with actual JWT parsing
        if (token != null && token.startsWith("STUB_TOKEN_")) {
            return token.substring("STUB_TOKEN_".length());
        }
        throw new IllegalArgumentException("Invalid stub token");
    }

    @Override
    public boolean validateToken(String token) {
        // TODO: Replace with actual JWT validation
        return token != null && token.startsWith("STUB_TOKEN_");
    }
}
