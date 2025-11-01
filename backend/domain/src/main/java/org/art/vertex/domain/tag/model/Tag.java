package org.art.vertex.domain.tag.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class Tag {

    UUID id;

    UUID userId;

    String name;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Integer version;

    public static Tag create(UUID id, UUID userId, String name, LocalDateTime ts) {
        return Tag.builder()
            .id(id)
            .userId(userId)
            .name(name)
            .createdAt(ts)
            .updatedAt(ts)
            .version(null)
            .build();
    }
}