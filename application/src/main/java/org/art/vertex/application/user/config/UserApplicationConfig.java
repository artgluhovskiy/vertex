package org.art.vertex.application.user.config;

import org.art.vertex.application.user.AuthApplicationService;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.domain.user.security.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UserApplicationConfig {

    @Bean
    public AuthApplicationService authApplicationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider,
        UuidGenerator uuidGenerator,
        Clock clock
    ) {
        return new AuthApplicationService(
            userRepository,
            passwordEncoder,
            jwtTokenProvider,
            uuidGenerator,
            clock
        );
    }

    @Bean
    public UserApplicationService userApplicationService(UserRepository userRepository) {
        return new UserApplicationService(userRepository);
    }
}

