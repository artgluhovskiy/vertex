package org.art.vertex.web.directory.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DirectoryDto(
    UUID id,
    UUID userId,
    String name,
    UUID parentId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
