package org.art.vertex.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.web.config.SecurityProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Production-ready JWT token provider using JJWT library.
 * Implements secure JWT token generation, validation, and parsing.
 */
@Slf4j
public class DefaultJwtTokenProvider implements JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final String issuer;
    private final Clock clock;

    public DefaultJwtTokenProvider(SecurityProperties.Jwt jwtProperties, Clock clock) {
        if (jwtProperties.getSecretKey() == null || jwtProperties.getSecretKey().isBlank()) {
            throw new IllegalArgumentException("JWT secret key must not be blank");
        }

        // Generate secure key from the configured secret
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.expirationMs = jwtProperties.getExpiration().toMillis();
        this.issuer = jwtProperties.getIssuer();
        this.clock = clock;

        log.debug("Initialized DefaultJwtTokenProvider with issuer: {}, expiration: {}ms", issuer, expirationMs);
    }

    @Override
    public String generateToken(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Instant now = clock.instant();
        Instant expiration = now.plusMillis(expirationMs);

        String token = Jwts.builder()
            .subject(user.getId().toString())
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .claim("email", user.getEmail())
            .signWith(secretKey)
            .compact();

        log.trace("Generated JWT token for user: {}", user.getId());

        return token;
    }

    @Override
    public String extractUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }

        try {
            Claims claims = parseToken(token);
            String userId = claims.getSubject();

            if (userId == null || userId.isBlank()) {
                log.warn("Token does not contain a valid subject (user ID)");
                throw new IllegalArgumentException("Invalid token: missing subject");
            }

            log.trace("Extracted user ID from token: {}", userId);
            return userId;
        } catch (ExpiredJwtException e) {
            log.debug("Attempted to extract user ID from expired token");
            throw new IllegalArgumentException("Token has expired", e);
        } catch (MalformedJwtException | UnsupportedJwtException | SignatureException e) {
            log.warn("Failed to extract user ID from invalid token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            log.debug("Token validation failed: token is null or blank");
            return false;
        }

        try {
            Claims claims = parseToken(token);

            // Additional validation: check issuer
            if (!issuer.equals(claims.getIssuer())) {
                log.warn("Token validation failed: issuer mismatch. Expected: {}, got: {}", issuer, claims.getIssuer());
                return false;
            }

            log.trace("Token validated successfully for user: {}", claims.getSubject());
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token validation failed: token has expired");
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token validation failed: malformed token - {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("Token validation failed: unsupported token format - {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Token validation failed: invalid signature");
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage(), e);
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .requireIssuer(issuer)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
