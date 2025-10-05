package org.art.vertex.infrastructure.user.config;

import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.user.DefaultUserRepository;
import org.art.vertex.infrastructure.user.jpa.UserJpaRepository;
import org.art.vertex.infrastructure.user.entity.UserEntity;
import org.art.vertex.infrastructure.user.mapper.UserEntityMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackageClasses = {
    UserJpaRepository.class
})
@EntityScan(basePackageClasses = {
    UserEntity.class
})
public class UserInfrastructureConfig {

    @Bean
    public UserEntityMapper userEntityMapper() {
        return new UserEntityMapper();
    }

    @Bean
    public UserRepository userRepository(UserJpaRepository userJpaRepository, UserEntityMapper userEntityMapper) {
        return new DefaultUserRepository(userJpaRepository, userEntityMapper);
    }
}
