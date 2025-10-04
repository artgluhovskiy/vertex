package org.art.vertex.infrastructure.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.exception.UserNotFoundException;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.user.model.UserSettings;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.user.entity.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserJpaRepository implements UserRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = toEntity(user);
        if (entityManager.contains(entity)) {
            entity = entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
        return toDomain(entity);
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
        UserEntity entity = entityManager.find(UserEntity.class, id);
        return Optional.ofNullable(entity).map(this::toDomain);
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
        try {
            UserEntity entity = entityManager
                .createQuery("SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getSingleResult();
            return Optional.of(toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        UserEntity entity = entityManager.find(UserEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        Long count = entityManager
            .createQuery("SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class)
            .setParameter("email", email)
            .getSingleResult();
        return count > 0;
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
            .id(user.getId())
            .email(user.getEmail())
            .passwordHash(user.getPasswordHash())
            .settings(user.getSettings().getPreferences())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .version(user.getVersion())
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
