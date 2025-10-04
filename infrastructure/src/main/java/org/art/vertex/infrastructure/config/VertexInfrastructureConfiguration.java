package org.art.vertex.infrastructure.config;

import jakarta.persistence.EntityManager;
import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.domain.user.security.PasswordEncoder;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.user.UserJpaRepository;
import org.art.vertex.infrastructure.user.security.StubJwtTokenProvider;
import org.art.vertex.infrastructure.user.security.StubPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class VertexInfrastructureConfiguration {

    @Bean
    public UserRepository userRepository(EntityManager entityManager) {
        return new UserJpaRepository(entityManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StubPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new StubJwtTokenProvider();
    }
}
