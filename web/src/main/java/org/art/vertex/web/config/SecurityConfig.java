package org.art.vertex.web.config;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.security.JwtTokenProvider;
import org.art.vertex.domain.user.security.PasswordEncoder;
import org.art.vertex.web.security.BCryptPasswordEncoder;
import org.art.vertex.web.security.JwtAuthenticationEntryPoint;
import org.art.vertex.web.security.JwtAuthenticationFilter;
import org.art.vertex.web.security.StubJwtTokenProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter,
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
    ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder springBCryptPasswordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(
            securityProperties.getBcrypt().getStrength()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder(
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder springBCryptPasswordEncoder
    ) {
        return new BCryptPasswordEncoder(springBCryptPasswordEncoder);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new StubJwtTokenProvider();
    }
}
