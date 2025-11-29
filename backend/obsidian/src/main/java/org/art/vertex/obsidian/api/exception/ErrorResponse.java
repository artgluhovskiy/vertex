package org.art.vertex.obsidian.api.exception;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Standard error response format for Obsidian API.
 */
@Value
@Builder
public class ErrorResponse {
    String error;
    String message;
    Instant timestamp;
}
