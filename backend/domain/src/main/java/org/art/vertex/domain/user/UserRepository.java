package org.art.vertex.domain.user;

import org.art.vertex.domain.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    User getById(UUID id);

    Optional<User> findById(UUID id);

    User getByEmail(String email);

    Optional<User> findByEmail(String email);

    void deleteById(UUID id);

    boolean existsByEmail(String email);
}
