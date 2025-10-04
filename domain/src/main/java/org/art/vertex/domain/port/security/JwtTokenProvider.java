package org.art.vertex.domain.port.security;

import org.art.vertex.domain.model.user.User;

public interface JwtTokenProvider {

    String generateToken(User user);

    String extractUserId(String token);

    boolean validateToken(String token);
}
