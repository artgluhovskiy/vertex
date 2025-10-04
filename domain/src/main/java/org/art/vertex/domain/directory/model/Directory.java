package org.art.vertex.domain.directory.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class Directory {

    UUID id;

    User user;

    String name;

    Directory parent;

    LocalDateTime createdTs;

    LocalDateTime updatedTs;

    // Version for the Optimistic Lock check
    Integer version;

    public static Directory create(
        UUID id,
        User user,
        String name,
        Directory parent,
        LocalDateTime ts
    ) {
        return Directory.builder()
            .id(id)
            .user(user)
            .name(name)
            .parent(parent)
            .createdTs(ts)
            .updatedTs(ts)
            .version(1)
            .build();
    }
}