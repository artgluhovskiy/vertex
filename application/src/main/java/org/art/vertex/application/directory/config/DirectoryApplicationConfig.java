package org.art.vertex.application.directory.config;

import org.art.vertex.application.directory.DirectoryApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DirectoryApplicationConfig {

    @Bean
    public DirectoryApplicationService directoryApplicationService() {
        return new DirectoryApplicationService();
    }
}
