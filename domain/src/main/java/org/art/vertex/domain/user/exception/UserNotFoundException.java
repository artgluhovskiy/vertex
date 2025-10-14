package org.art.vertex.domain.user.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("User not found. User id: %s".formatted(userId));
    }

    public UserNotFoundException(UUID userId) {
        super("User not found. User id: %s".formatted(userId));
    }
}
