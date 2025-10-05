package org.art.vertex.web.user.config;

import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.web.user.AuthController;
import org.art.vertex.web.user.mapper.UserDtoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UserWebConfig {

    @Bean
    public UserDtoMapper userDtoMapper() {
        return new UserDtoMapper();
    }

    @Bean
    public AuthController authController(
        UserApplicationService userApplicationService,
        UserDtoMapper userDtoMapper
    ) {
        return new AuthController(userApplicationService, userDtoMapper);
    }
}
