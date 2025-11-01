package org.art.vertex.web.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp,
    String path
) {
}
