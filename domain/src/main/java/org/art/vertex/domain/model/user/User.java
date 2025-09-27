package org.art.vertex.domain.model.user;

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

    public static User create(String email, String passwordHash, LocalDateTime ts) {
        return User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .passwordHash(passwordHash)
            .settings(UserSettings.defaultSettings())
            .updatedAt(ts)
            .createdAt(ts)
            .version(1)
            .build();
    }
}