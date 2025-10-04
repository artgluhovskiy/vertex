package org.art.vertex.infrastructure.user;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.exception.UserNotFoundException;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.user.model.UserSettings;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.user.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserJpaRepository implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = userEntityRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userEntityRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userEntityRepository.findByEmail(email)
            .map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userEntityRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmail(email);
    }

    private UserEntity toEntity(User user) {
        // Check if this is a new entity (not yet persisted)
        boolean isNew = !userEntityRepository.existsById(user.getId());

        return UserEntity.builder()
            .id(user.getId())
            .email(user.getEmail())
            .passwordHash(user.getPasswordHash())
            .settings(user.getSettings().getPreferences())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            // For new entities, don't set version (let JPA manage it)
            // For existing entities, use the version from domain model
            .version(isNew ? null : user.getVersion())
            .build();
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .passwordHash(entity.getPasswordHash())
            .settings(UserSettings.builder()
                .preferences(entity.getSettings())
                .build())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}
