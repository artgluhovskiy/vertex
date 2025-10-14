package org.art.vertex.domain.user.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class User {

    UUID id;

    String email;

    String passwordHash;

    UserSettings settings;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    // Version for the Optimistic Lock check
    Integer version;

    public static User create(UUID id, String email, String passwordHash, LocalDateTime ts) {
        return User.builder()
            .id(id)
            .email(email)
            .passwordHash(passwordHash)
            .settings(UserSettings.defaultSettings())
            .updatedAt(ts)
            .createdAt(ts)
            .version(null)
            .build();
    }
}