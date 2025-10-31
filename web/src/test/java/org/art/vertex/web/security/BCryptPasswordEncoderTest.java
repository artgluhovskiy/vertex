package org.art.vertex.web.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BCryptPasswordEncoder Unit Tests")
class BCryptPasswordEncoderTest {

    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        var springEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12);
        encoder = new BCryptPasswordEncoder(springEncoder);
    }

    @Test
    @DisplayName("Should encode password successfully with default strength")
    void shouldEncodePasswordWithDefaultStrength() {
        // GIVEN
        var rawPassword = "mySecurePassword123!";

        // WHEN
        var encodedPassword = encoder.encode(rawPassword);

        // THEN
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encodedPassword).startsWith("$2a$12$");
        assertThat(encodedPassword).hasSize(60);
    }

    @Test
    @DisplayName("Should encode password successfully with custom strength")
    void shouldEncodePasswordWithCustomStrength() {
        // GIVEN
        var springEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(10);
        var customEncoder = new BCryptPasswordEncoder(springEncoder);
        var rawPassword = "mySecurePassword123!";

        // WHEN
        var encodedPassword = customEncoder.encode(rawPassword);

        // THEN
        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).startsWith("$2a$10$");
        assertThat(encodedPassword).hasSize(60);
    }

    @Test
    @DisplayName("Should produce different hashes for same password due to random salt")
    void shouldProduceDifferentHashesForSamePassword() {
        // GIVEN
        var rawPassword = "mySecurePassword123!";

        // WHEN
        var encodedPassword1 = encoder.encode(rawPassword);
        var encodedPassword2 = encoder.encode(rawPassword);

        // THEN
        assertThat(encodedPassword1).isNotEqualTo(encodedPassword2);
        assertThat(encoder.matches(rawPassword, encodedPassword1)).isTrue();
        assertThat(encoder.matches(rawPassword, encodedPassword2)).isTrue();
    }

    @Test
    @DisplayName("Should match raw password with encoded password")
    void shouldMatchRawPasswordWithEncodedPassword() {
        // GIVEN
        var rawPassword = "mySecurePassword123!";
        var encodedPassword = encoder.encode(rawPassword);

        // WHEN
        var result = encoder.matches(rawPassword, encodedPassword);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should not match wrong password")
    void shouldNotMatchWrongPassword() {
        // GIVEN
        var rawPassword = "mySecurePassword123!";
        var wrongPassword = "wrongPassword456!";
        var encodedPassword = encoder.encode(rawPassword);

        // WHEN
        var result = encoder.matches(wrongPassword, encodedPassword);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should not match when raw password is null")
    void shouldNotMatchWhenRawPasswordIsNull() {
        // GIVEN
        var encodedPassword = "$2a$12$validHashHereWithCorrectFormat1234567890ABC";

        // WHEN
        var result = encoder.matches(null, encodedPassword);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should not match when encoded password is null")
    void shouldNotMatchWhenEncodedPasswordIsNull() {
        // WHEN
        var result = encoder.matches("somePassword", null);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should not match when encoded password is empty")
    void shouldNotMatchWhenEncodedPasswordIsEmpty() {
        // WHEN
        var result = encoder.matches("somePassword", "");

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should not match when encoded password has invalid format")
    void shouldNotMatchWhenEncodedPasswordHasInvalidFormat() {
        // WHEN
        var result = encoder.matches("somePassword", "invalidHashFormat");

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when encoding null password")
    void shouldThrowExceptionWhenEncodingNullPassword() {
        // WHEN & THEN
        assertThatThrownBy(() -> encoder.encode(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Raw password cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when encoding empty password")
    void shouldThrowExceptionWhenEncodingEmptyPassword() {
        // WHEN & THEN
        assertThatThrownBy(() -> encoder.encode(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Raw password cannot be empty");
    }

    @Test
    @DisplayName("Should handle minimum valid strength")
    void shouldHandleMinimumValidStrength() {
        // GIVEN
        var springEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(4);
        var customEncoder = new BCryptPasswordEncoder(springEncoder);
        var rawPassword = "password";

        // WHEN
        var encodedPassword = customEncoder.encode(rawPassword);

        // THEN
        assertThat(encodedPassword).startsWith("$2a$04$");
        assertThat(customEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("Should handle higher valid strength")
    void shouldHandleHigherValidStrength() {
        // GIVEN
        var springEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(14);
        var customEncoder = new BCryptPasswordEncoder(springEncoder);
        var rawPassword = "password";

        // WHEN
        var encodedPassword = customEncoder.encode(rawPassword);

        // THEN
        assertThat(encodedPassword).startsWith("$2a$14$");
        assertThat(customEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void shouldHandleSpecialCharactersInPassword() {
        // GIVEN
        var rawPassword = "p@ssw0rd!@#$%^&*()_+-=[]{}|;:',.<>?/~`";

        // WHEN
        var encodedPassword = encoder.encode(rawPassword);

        // THEN
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("Should handle unicode characters in password")
    void shouldHandleUnicodeCharactersInPassword() {
        // GIVEN
        var rawPassword = "пароль密码パスワード🔐";

        // WHEN
        var encodedPassword = encoder.encode(rawPassword);

        // THEN
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("Should handle passwords up to 72 characters")
    void shouldHandlePasswordsUpTo72Characters() {
        // GIVEN
        var rawPassword = "a".repeat(72);

        // WHEN
        var encodedPassword = encoder.encode(rawPassword);

        // THEN
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("Should be case sensitive")
    void shouldBeCaseSensitive() {
        // GIVEN
        var rawPassword = "MyPassword";
        var encodedPassword = encoder.encode(rawPassword);

        // WHEN
        var matchesCorrectCase = encoder.matches("MyPassword", encodedPassword);
        var matchesWrongCase = encoder.matches("mypassword", encodedPassword);

        // THEN
        assertThat(matchesCorrectCase).isTrue();
        assertThat(matchesWrongCase).isFalse();
    }

    @Test
    @DisplayName("Should handle whitespace in passwords")
    void shouldHandleWhitespaceInPasswords() {
        // GIVEN
        var rawPassword = "my password with spaces";

        // WHEN
        var encodedPassword = encoder.encode(rawPassword);

        // THEN
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(encoder.matches("my password with  spaces", encodedPassword)).isFalse();
    }

    @Test
    @DisplayName("Should work with hashes generated by same encoder")
    void shouldWorkWithHashesGeneratedBySameEncoder() {
        // GIVEN
        var rawPassword = "testPassword123";

        // Generate a hash using the same encoder
        var generatedHash = encoder.encode(rawPassword);

        // WHEN - Verify the hash matches
        var result = encoder.matches(rawPassword, generatedHash);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should not match different password with generated hash")
    void shouldNotMatchDifferentPasswordWithGeneratedHash() {
        // GIVEN
        var rawPassword = "testPassword123";
        var wrongPassword = "wrongPassword123";
        var generatedHash = encoder.encode(rawPassword);

        // WHEN
        var result = encoder.matches(wrongPassword, generatedHash);

        // THEN
        assertThat(result).isFalse();
    }
}
