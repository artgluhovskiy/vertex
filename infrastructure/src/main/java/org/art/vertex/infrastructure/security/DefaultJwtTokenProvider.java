package org.art.vertex.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.User;
import org.art.vertex.domain.shared.port.security.JwtTokenProvider;

@RequiredArgsConstructor
public class DefaultJwtTokenProvider implements JwtTokenProvider {

    @Override
    public String generateToken(User user) {
        // TODO: Implement JWT token generation using io.jsonwebtoken library
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String extractUserId(String token) {
        // TODO: Implement user ID extraction from JWT token
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean validateToken(String token) {
        // TODO: Implement JWT token validation
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
