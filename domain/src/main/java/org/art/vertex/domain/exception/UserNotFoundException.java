package org.art.vertex.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("User not found. User id: %s".formatted(userId));
    }

    public UserNotFoundException(String field, String value) {
        super("User not found. %s: %s".formatted(field, value));
    }
}
