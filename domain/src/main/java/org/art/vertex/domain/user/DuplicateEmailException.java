package org.art.vertex.domain.user;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("User with this email already exists. Email: %s".formatted(email));
    }
}
