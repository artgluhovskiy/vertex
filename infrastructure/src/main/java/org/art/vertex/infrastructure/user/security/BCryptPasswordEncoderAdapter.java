package org.art.vertex.infrastructure.user.security;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.security.PasswordEncoder;

@RequiredArgsConstructor
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        // TODO: Implement password encoding using Spring Security BCryptPasswordEncoder
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        // TODO: Implement password matching using Spring Security BCryptPasswordEncoder
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
