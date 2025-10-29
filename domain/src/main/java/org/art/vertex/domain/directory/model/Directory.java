package org.art.vertex.domain.directory.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class Directory {

    UUID id;

    UUID userId;

    String name;

    Directory parent;

    LocalDateTime createdTs;

    LocalDateTime updatedTs;

    // Version for the Optimistic Lock check
    Integer version;

    public static Directory create(
        UUID id,
        UUID userId,
        String name,
        Directory parent,
        LocalDateTime ts
    ) {
        return Directory.builder()
            .id(id)
            .userId(userId)
            .name(name)
            .parent(parent)
            .createdTs(ts)
            .updatedTs(ts)
            .version(1)
            .build();
    }
}
