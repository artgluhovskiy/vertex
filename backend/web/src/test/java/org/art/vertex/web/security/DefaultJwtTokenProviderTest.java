package org.art.vertex.web.security;

import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.web.config.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultJwtTokenProviderTest {

    private DefaultJwtTokenProvider tokenProvider;
    private SecurityProperties.Jwt jwtProperties;
    private User testUser;
    private Clock clock;

    @BeforeEach
    void setUp() {
        jwtProperties = new SecurityProperties.Jwt();
        jwtProperties.setSecretKey("test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-256");
        jwtProperties.setExpiration(Duration.ofMinutes(30));
        jwtProperties.setIssuer("vertex-test");

        clock = new Clock(java.time.Clock.systemUTC());
        tokenProvider = new DefaultJwtTokenProvider(jwtProperties, clock);

        testUser = User.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .passwordHash("hashed-password")
            .build();
    }

    // Token Generation Tests

    @Test
    void shouldGenerateValidToken() {
        String token = tokenProvider.generateToken(testUser);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    void shouldGenerateUniqueTokensForSameUser() throws InterruptedException {
        String token1 = tokenProvider.generateToken(testUser);
        Thread.sleep(1100); // Ensure different timestamps (JWT uses seconds, so need >1000ms)
        String token2 = tokenProvider.generateToken(testUser);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentUsers() {
        User anotherUser = User.builder()
            .id(UUID.randomUUID())
            .email("another@example.com")
            .passwordHash("hashed-password")
            .build();

        String token1 = tokenProvider.generateToken(testUser);
        String token2 = tokenProvider.generateToken(anotherUser);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        assertThatThrownBy(() -> tokenProvider.generateToken(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        User userWithoutId = User.builder()
            .email("test@example.com")
            .passwordHash("hashed-password")
            .build();

        assertThatThrownBy(() -> tokenProvider.generateToken(userWithoutId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User ID cannot be null");
    }

    // Token Validation Tests

    @Test
    void shouldValidateCorrectToken() {
        String token = tokenProvider.generateToken(testUser);

        boolean isValid = tokenProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void shouldRejectNullToken() {
        boolean isValid = tokenProvider.validateToken(null);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectEmptyToken() {
        boolean isValid = tokenProvider.validateToken("");

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectBlankToken() {
        boolean isValid = tokenProvider.validateToken("   ");

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectMalformedToken() {
        boolean isValid = tokenProvider.validateToken("not.a.valid.jwt.token");

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectTokenWithInvalidSignature() {
        String token = tokenProvider.generateToken(testUser);
        // Tamper with the token signature
        String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "tampered";

        boolean isValid = tokenProvider.validateToken(tamperedToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectExpiredToken() {
        // Create provider with very short expiration
        SecurityProperties.Jwt shortExpirationProps = new SecurityProperties.Jwt();
        shortExpirationProps.setSecretKey("test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-256");
        shortExpirationProps.setExpiration(Duration.ofMillis(1));
        shortExpirationProps.setIssuer("vertex-test");

        DefaultJwtTokenProvider shortExpirationProvider = new DefaultJwtTokenProvider(shortExpirationProps, clock);
        String token = shortExpirationProvider.generateToken(testUser);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isValid = shortExpirationProvider.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    void shouldRejectTokenFromDifferentIssuer() {
        // Create provider with different issuer
        SecurityProperties.Jwt differentIssuerProps = new SecurityProperties.Jwt();
        differentIssuerProps.setSecretKey("test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-256");
        differentIssuerProps.setExpiration(Duration.ofMinutes(30));
        differentIssuerProps.setIssuer("different-issuer");

        DefaultJwtTokenProvider differentIssuerProvider = new DefaultJwtTokenProvider(differentIssuerProps, clock);
        String token = differentIssuerProvider.generateToken(testUser);

        // Validate with original provider (different issuer)
        boolean isValid = tokenProvider.validateToken(token);

        assertThat(isValid).isFalse();
    }

    // User ID Extraction Tests

    @Test
    void shouldExtractCorrectUserId() {
        String token = tokenProvider.generateToken(testUser);

        String extractedUserId = tokenProvider.extractUserId(token);

        assertThat(extractedUserId).isEqualTo(testUser.getId().toString());
    }

    @Test
    void shouldThrowExceptionWhenExtractingFromNullToken() {
        assertThatThrownBy(() -> tokenProvider.extractUserId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Token cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenExtractingFromEmptyToken() {
        assertThatThrownBy(() -> tokenProvider.extractUserId(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Token cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenExtractingFromBlankToken() {
        assertThatThrownBy(() -> tokenProvider.extractUserId("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Token cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenExtractingFromMalformedToken() {
        assertThatThrownBy(() -> tokenProvider.extractUserId("not.a.valid.jwt"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid token");
    }

    @Test
    void shouldThrowExceptionWhenExtractingFromExpiredToken() {
        // Create provider with very short expiration
        SecurityProperties.Jwt shortExpirationProps = new SecurityProperties.Jwt();
        shortExpirationProps.setSecretKey("test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-256");
        shortExpirationProps.setExpiration(Duration.ofMillis(1));
        shortExpirationProps.setIssuer("vertex-test");

        DefaultJwtTokenProvider shortExpirationProvider = new DefaultJwtTokenProvider(shortExpirationProps, clock);
        String token = shortExpirationProvider.generateToken(testUser);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThatThrownBy(() -> tokenProvider.extractUserId(token))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Token has expired");
    }

    @Test
    void shouldThrowExceptionWhenExtractingFromTokenWithInvalidSignature() {
        String token = tokenProvider.generateToken(testUser);
        String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "tampered";

        assertThatThrownBy(() -> tokenProvider.extractUserId(tamperedToken))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid token");
    }

    // Configuration Validation Tests

    @Test
    void shouldThrowExceptionWhenSecretKeyIsNull() {
        SecurityProperties.Jwt invalidProps = new SecurityProperties.Jwt();
        invalidProps.setSecretKey(null);
        invalidProps.setExpiration(Duration.ofMinutes(30));
        invalidProps.setIssuer("vertex-test");

        assertThatThrownBy(() -> new DefaultJwtTokenProvider(invalidProps, clock))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("JWT secret key must not be blank");
    }

    @Test
    void shouldThrowExceptionWhenSecretKeyIsEmpty() {
        SecurityProperties.Jwt invalidProps = new SecurityProperties.Jwt();
        invalidProps.setSecretKey("");
        invalidProps.setExpiration(Duration.ofMinutes(30));
        invalidProps.setIssuer("vertex-test");

        assertThatThrownBy(() -> new DefaultJwtTokenProvider(invalidProps, clock))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("JWT secret key must not be blank");
    }

    @Test
    void shouldThrowExceptionWhenSecretKeyIsBlank() {
        SecurityProperties.Jwt invalidProps = new SecurityProperties.Jwt();
        invalidProps.setSecretKey("   ");
        invalidProps.setExpiration(Duration.ofMinutes(30));
        invalidProps.setIssuer("vertex-test");

        assertThatThrownBy(() -> new DefaultJwtTokenProvider(invalidProps, clock))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("JWT secret key must not be blank");
    }

    // Integration Tests

    @Test
    void shouldCompleteFullTokenLifecycle() {
        // Generate token
        String token = tokenProvider.generateToken(testUser);
        assertThat(token).isNotNull();

        // Validate token
        boolean isValid = tokenProvider.validateToken(token);
        assertThat(isValid).isTrue();

        // Extract user ID
        String extractedUserId = tokenProvider.extractUserId(token);
        assertThat(extractedUserId).isEqualTo(testUser.getId().toString());
    }

    @Test
    void shouldHandleMultipleUsersCorrectly() {
        User user1 = User.builder()
            .id(UUID.randomUUID())
            .email("user1@example.com")
            .passwordHash("hash1")
            .build();

        User user2 = User.builder()
            .id(UUID.randomUUID())
            .email("user2@example.com")
            .passwordHash("hash2")
            .build();

        String token1 = tokenProvider.generateToken(user1);
        String token2 = tokenProvider.generateToken(user2);

        assertThat(tokenProvider.validateToken(token1)).isTrue();
        assertThat(tokenProvider.validateToken(token2)).isTrue();

        assertThat(tokenProvider.extractUserId(token1)).isEqualTo(user1.getId().toString());
        assertThat(tokenProvider.extractUserId(token2)).isEqualTo(user2.getId().toString());
    }
}
