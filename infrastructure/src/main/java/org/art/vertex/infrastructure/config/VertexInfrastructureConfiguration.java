package org.art.vertex.infrastructure.config;

import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.domain.user.security.PasswordEncoder;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.user.UserJpaRepository;
import org.art.vertex.infrastructure.user.security.BCryptPasswordEncoderAdapter;
import org.art.vertex.infrastructure.user.security.DefaultJwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class VertexInfrastructureConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoderAdapter();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new DefaultJwtTokenProvider();
    }

    @Bean
    public UserRepository userRepository() {
        return new UserJpaRepository();
    }
}
