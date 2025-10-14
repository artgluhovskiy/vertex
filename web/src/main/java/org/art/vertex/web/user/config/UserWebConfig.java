package org.art.vertex.web.user.config;

import org.art.vertex.application.user.AuthApplicationService;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.web.user.AuthController;
import org.art.vertex.web.user.mapper.UserCommandMapper;
import org.art.vertex.web.user.mapper.UserDtoMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UserWebConfig {

    @Bean
    public UserCommandMapper userCommandMapper() {
        return new UserCommandMapper();
    }

    @Bean
    public UserDtoMapper userDtoMapper() {
        return new UserDtoMapper();
    }

    @Bean
    public AuthController authController(
        AuthApplicationService authApplicationService,
        UserApplicationService userApplicationService,
        UserCommandMapper userCommandMapper,
        UserDtoMapper userDtoMapper
    ) {
        return new AuthController(
            authApplicationService,
            userApplicationService,
            userCommandMapper,
            userDtoMapper
        );
    }
}
