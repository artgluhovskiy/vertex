package org.art.vertex.application.config;

import org.art.vertex.application.mapper.UserMapper;
import org.art.vertex.application.service.UserApplicationService;
import org.art.vertex.domain.shared.port.security.JwtTokenProvider;
import org.art.vertex.domain.shared.port.security.PasswordEncoder;
import org.art.vertex.domain.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class VertexApplicationConfiguration {

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public UserApplicationService userApplicationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider,
        UserMapper userMapper
    ) {
        return new UserApplicationService(userRepository, passwordEncoder, jwtTokenProvider, userMapper);
    }
}

