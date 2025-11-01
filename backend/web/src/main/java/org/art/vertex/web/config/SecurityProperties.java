package org.art.vertex.web.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configuration properties for security settings.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private Bcrypt bcrypt = new Bcrypt();

    private Jwt jwt = new Jwt();

    @Data
    public static class Bcrypt {

        /**
         * BCrypt strength (work factor) for password hashing.
         * Higher values increase security but also increase computation time.
         * Valid range: 4-31, recommended: 10-12 for production.
         */
        @Min(value = 4, message = "BCrypt strength must be at least 4")
        @Max(value = 31, message = "BCrypt strength must not exceed 31")
        private int strength = 12;
    }

    @Data
    public static class Jwt {

        /**
         * Secret key for signing JWT tokens.
         * Must be at least 256 bits (32 bytes) for HS256 algorithm.
         * IMPORTANT: This should be externalized to environment variables in production.
         */
        @NotBlank(message = "JWT secret key must not be blank")
        private String secretKey;

        /**
         * JWT token expiration time.
         * Default: 24 hours
         */
        private Duration expiration = Duration.ofHours(24);

        /**
         * JWT token issuer.
         */
        private String issuer = "vertex";
    }
}
