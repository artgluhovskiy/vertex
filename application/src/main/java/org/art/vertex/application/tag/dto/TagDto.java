package org.art.vertex.application.tag.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class TagDto {
    UUID id;
    String name;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}