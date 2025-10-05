package org.art.vertex.infrastructure.user.mapper;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.user.model.UserSettings;
import org.art.vertex.infrastructure.user.entity.UserEntity;

@RequiredArgsConstructor
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
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
