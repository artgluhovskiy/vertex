package org.art.vertex.application.tag.config;

import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.tag.TagRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TagApplicationConfig {

    @Bean
    public TagApplicationService tagApplicationService(
        TagRepository tagRepository,
        UuidGenerator uuidGenerator,
        Clock clock
    ) {
        return new TagApplicationService(tagRepository, uuidGenerator, clock);
    }
}
