package org.art.vertex.application.directory.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class DirectoryDto {
    UUID id;
    UUID userId;
    String name;
    UUID parentId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer version;
}