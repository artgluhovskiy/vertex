package org.art.vertex.application.tag.config;

import org.art.vertex.application.tag.TagApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TagApplicationConfig {

    @Bean
    public TagApplicationService tagApplicationService() {
        return new TagApplicationService();
    }
}

