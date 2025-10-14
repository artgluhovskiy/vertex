package org.art.vertex.application.user.config;

import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.domain.user.security.PasswordEncoder;
import org.art.vertex.domain.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UserApplicationConfig {

    @Bean
    public UserApplicationService userApplicationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider,
        UuidGenerator uuidGenerator,
        Clock clock
    ) {
        return new UserApplicationService(userRepository, passwordEncoder, jwtTokenProvider, uuidGenerator, clock);
    }
}

