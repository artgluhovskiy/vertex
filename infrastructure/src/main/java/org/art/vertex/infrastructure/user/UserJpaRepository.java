package org.art.vertex.infrastructure.user;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.User;
import org.art.vertex.domain.user.UserRepository;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserJpaRepository implements UserRepository {

    @Override
    public User save(User user) {
        // TODO: Implement user save using JPA EntityManager
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User getById(UUID id) {
        // TODO: Implement get user by ID using JPA EntityManager
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<User> findById(UUID id) {
        // TODO: Implement find user by ID using JPA EntityManager
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User getByEmail(String email) {
        // TODO: Implement get user by email using JPA EntityManager
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // TODO: Implement find user by email using JPA EntityManager
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement delete user by ID using JPA EntityManager
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean existsByEmail(String email) {
        // TODO: Implement check if user exists by email using JPA EntityManager
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
