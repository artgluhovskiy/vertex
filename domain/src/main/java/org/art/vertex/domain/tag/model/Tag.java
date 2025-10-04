package org.art.vertex.domain.tag.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class Tag {

    UUID id;

    User user;

    String name;

    String description;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Integer version;

    public static Tag create(User user, String name, LocalDateTime ts) {
        return Tag.builder()
            .id(UUID.randomUUID())
            .user(user)
            .name(name)
            .createdAt(ts)
            .updatedAt(ts)
            .version(1)
            .build();
    }
}