package org.art.vertex.web.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for security settings.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private Bcrypt bcrypt = new Bcrypt();

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
}
