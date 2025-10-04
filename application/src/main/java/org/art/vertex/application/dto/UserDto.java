package org.art.vertex.application.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDto(
    String id,
    String email,
    LocalDateTime createdAt
) {
}
