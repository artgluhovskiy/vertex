package org.art.vertex.obsidian.api.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.obsidian.infrastructure.reader.ObsidianFileReadException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Global exception handler for Obsidian API endpoints.
 */
@Slf4j
@RestControllerAdvice(basePackages = "org.art.vertex.obsidian.api")
@RequiredArgsConstructor
public class ObsidianApiExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(ObsidianFileReadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleObsidianFileReadException(ObsidianFileReadException e) {
        log.warn("Obsidian file read error: {}", e.getMessage());
        return ErrorResponse.builder()
            .error("OBSIDIAN_FILE_READ_ERROR")
            .message(e.getMessage())
            .timestamp(Instant.from(clock.now()))
            .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Invalid migration request: {}", e.getMessage());
        return ErrorResponse.builder()
            .error("INVALID_REQUEST")
            .message(e.getMessage())
            .timestamp(Instant.from(clock.now()))
            .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception e) {
        log.error("Unexpected error during Obsidian migration", e);
        return ErrorResponse.builder()
            .error("MIGRATION_ERROR")
            .message("An unexpected error occurred during migration. Please try again or contact support.")
            .timestamp(Instant.from(clock.now()))
            .build();
    }
}
