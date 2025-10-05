package org.art.vertex.web.user.config;

import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.web.user.AuthController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UserWebConfig {

    @Bean
    public AuthController authController(UserApplicationService userApplicationService) {
        return new AuthController(userApplicationService);
    }
}
