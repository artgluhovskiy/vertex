package org.art.vertex.domain.user.security;

import org.art.vertex.domain.user.model.User;

public interface JwtTokenProvider {

    String generateToken(User user);

    String extractUserId(String token);

    boolean validateToken(String token);
}
