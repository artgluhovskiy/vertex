package org.art.vertex.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.exception.NoteNotFoundException;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.user.exception.DuplicateEmailException;
import org.art.vertex.domain.user.exception.UserNotFoundException;
import org.art.vertex.domain.user.security.exception.InvalidCredentialsException;
import org.art.vertex.domain.user.security.exception.InvalidTokenException;
import org.art.vertex.web.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "org.art.vertex.web")
public class RestExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException e, HttpServletRequest request) {
        log.warn("User not found. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message(e.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .timestamp(clock.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(NoteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoteNotFound(NoteNotFoundException e, HttpServletRequest request) {
        log.warn("Note not found. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message(e.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .timestamp(clock.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(DuplicateEmailException e, HttpServletRequest request) {
        log.warn("Duplicate email. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message(e.getMessage())
            .status(HttpStatus.CONFLICT.value())
            .timestamp(clock.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest request) {
        log.warn("Invalid credentials. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message(e.getMessage())
            .status(HttpStatus.UNAUTHORIZED.value())
            .timestamp(clock.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidToken(InvalidTokenException e, HttpServletRequest request) {
        log.warn("Invalid token. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message(e.getMessage())
            .status(HttpStatus.UNAUTHORIZED.value())
            .timestamp(clock.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        var errors = e.getBindingResult().getFieldErrors().stream()
            .map(error -> "Field '%s': %s".formatted(error.getField(), error.getDefaultMessage()))
            .toList();

        log.warn("Validation failed. Errors: {}", errors);

        return ErrorResponse.builder()
            .message("Validation failed: " + String.join(", ", errors))
            .status(HttpStatus.BAD_REQUEST.value())
            .timestamp(clock.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception e, HttpServletRequest request) {
        log.error("Unexpected error occurred. Path: {}", request.getRequestURI(), e);
        return ErrorResponse.builder()
            .message("An unexpected error occurred")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(clock.now())
            .path(request.getRequestURI())
            .build();
    }
}
