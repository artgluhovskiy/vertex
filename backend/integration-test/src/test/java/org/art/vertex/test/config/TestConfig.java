package org.art.vertex.test.config;

import org.art.vertex.test.step.DirSteps;
import org.art.vertex.test.step.NoteSteps;
import org.art.vertex.test.step.ObsidianMigrationSteps;
import org.art.vertex.test.step.SearchSteps;
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

    @Bean
    public DirSteps dirSteps() {
        return new DirSteps();
    }

    @Bean
    public SearchSteps searchSteps() {
        return new SearchSteps();
    }

    @Bean
    public ObsidianMigrationSteps obsidianMigrationSteps() {
        return new ObsidianMigrationSteps();
    }
}

