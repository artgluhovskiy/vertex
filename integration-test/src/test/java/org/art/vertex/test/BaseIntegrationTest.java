package org.art.vertex.test;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.VertexApplication;
import org.art.vertex.test.container.TestContainerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for all integration tests.
 * Uses IntegrationTestConfiguration which scans all org.art.vertex packages,
 * providing the same configuration as the main VertexApplication.
 */
@Slf4j
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = VertexApplication.class
)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    private static final TestContainerManager containerManager = TestContainerManager.getInstance();

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * Configure dynamic properties for test containers.
     * This method is called before the Spring context is created.
     */
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        containerManager.initializeContainers();

        registry.add("spring.datasource.url", () ->
            "jdbc:postgresql://" + containerManager.getPostgresHost() + ":"
                + containerManager.getPostgresPort() + "/vertex");
        registry.add("spring.datasource.username", () -> "vertex");
        registry.add("spring.datasource.password", () -> "vertex");
    }

    @BeforeAll
    static void setUpIntegrationTest() {
        log.info("🚀 Setting up integration test environment...");

        containerManager.initializeContainers();

        if (!containerManager.areAllContainersAccessible()) {
            throw new IllegalStateException("❌ Containers are not accessible. Tests cannot proceed.");
        }
    }

    @AfterEach
    void cleanupAfterTest() {
        cleanupAllTestData();
    }

    protected void cleanupAllTestData() {
        try {
            // Disable foreign key checks temporarily for truncation
            jdbcTemplate.execute("SET session_replication_role = 'replica'");

            // Truncate all tables (add more as schema grows)
            jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");

            // Re-enable foreign key checks
            jdbcTemplate.execute("SET session_replication_role = 'origin'");
        } catch (Exception e) {
            log.error("⚠️  Error during test data cleanup: {}", e.getMessage(), e);
            throw e;
        }
    }
}
