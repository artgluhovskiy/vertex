package org.art.vertex.web.user.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDto(
    String id,
    String email,
    LocalDateTime createdAt
) {
}
