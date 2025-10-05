package org.art.vertex.infrastructure.config;

import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.domain.user.security.PasswordEncoder;
import org.art.vertex.infrastructure.user.UserJpaRepository;
import org.art.vertex.infrastructure.user.DefaultUserRepository;
import org.art.vertex.infrastructure.user.mapper.UserEntityMapper;
import org.art.vertex.infrastructure.user.security.StubJwtTokenProvider;
import org.art.vertex.infrastructure.user.security.StubPasswordEncoder;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = "org.art.vertex.infrastructure")
@EntityScan(basePackages = "org.art.vertex.infrastructure")
public class VertexInfrastructureConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StubPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new StubJwtTokenProvider();
    }

    @Bean
    public UserEntityMapper userEntityMapper() {
        return new UserEntityMapper();
    }

    @Bean
    public UserRepository userRepository(UserJpaRepository userJpaRepository, UserEntityMapper userEntityMapper) {
        return new DefaultUserRepository(userJpaRepository, userEntityMapper);
    }
}
