package org.art.vertex.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * Test configuration for async task execution.
 * Uses SyncTaskExecutor to make @Async methods execute synchronously in tests,
 * ensuring deterministic test execution while still testing the event-driven flow.
 */
@Configuration
public class TestAsyncConfig {

    /**
     * Provides a SyncTaskExecutor that executes tasks synchronously in the same thread.
     * This makes @Async event listeners execute synchronously in tests, avoiding race conditions
     * and making tests deterministic.
     */
    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}
