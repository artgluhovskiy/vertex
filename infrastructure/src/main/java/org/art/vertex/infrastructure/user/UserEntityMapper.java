package org.art.vertex.infrastructure.user;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.user.model.UserSettings;
import org.art.vertex.infrastructure.user.entity.UserEntity;

@RequiredArgsConstructor
public class UserEntityMapper {

    private final UserEntityRepository userEntityRepository;

    public UserEntity toEntity(User user) {
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

    public User toDomain(UserEntity entity) {
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
