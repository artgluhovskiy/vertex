package org.art.vertex.test.config;

import org.art.vertex.test.step.NoteSteps;
import org.art.vertex.test.step.UserSteps;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public UserSteps userSteps() {
        return new UserSteps();
    }

    @Bean
    public NoteSteps noteSteps() {
        return new NoteSteps();
    }
}
