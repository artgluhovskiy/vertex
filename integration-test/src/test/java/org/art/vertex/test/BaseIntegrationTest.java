package org.art.vertex.test;

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

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = VertexApplication.class
)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    private static final TestContainerManager containerManager = TestContainerManager.getInstance();

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    /**
     * Set up the integration test environment once before all tests.
     */
    @BeforeAll
    static void setUpIntegrationTest() {
        System.out.println("🚀 Setting up integration test environment...");
        containerManager.initializeContainers();
        System.out.println(containerManager.getContainerStatus());

        if (!containerManager.areAllContainersAccessible()) {
            throw new IllegalStateException("❌ Containers are not accessible. Tests cannot proceed.");
        }
    }

    /**
     * Clean up test data after each test to ensure isolation.
     */
    @AfterEach
    void cleanupAfterTest() {
        cleanupAllTestData();
    }

    /**
     * Truncate all tables to ensure clean state between tests.
     * Add new tables here as they are created.
     */
    protected void cleanupAllTestData() {
        try {
            // Disable foreign key checks temporarily for truncation
            jdbcTemplate.execute("SET session_replication_role = 'replica'");

            // Truncate all tables (add more as schema grows)
            jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");

            // Re-enable foreign key checks
            jdbcTemplate.execute("SET session_replication_role = 'origin'");
        } catch (Exception e) {
            System.err.println("⚠️  Error during test data cleanup: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get JDBC template for custom database operations in tests.
     */
    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * Get container manager for advanced container operations.
     */
    protected TestContainerManager getContainerManager() {
        return containerManager;
    }
}
