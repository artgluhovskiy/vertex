package org.art.vertex.test;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.VertexApplication;
import org.art.vertex.test.config.TestConfig;
import org.art.vertex.test.container.TestContainerManager;
import org.art.vertex.test.step.DirSteps;
import org.art.vertex.test.step.NoteSteps;
import org.art.vertex.test.step.SearchSteps;
import org.art.vertex.test.step.UserSteps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Slf4j
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {VertexApplication.class, TestConfig.class}
)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    private static final TestContainerManager containerManager = TestContainerManager.getInstance();

    @Autowired
    protected UserSteps userSteps;

    @Autowired
    protected NoteSteps noteSteps;

    @Autowired
    protected DirSteps dirSteps;

    @Autowired
    protected SearchSteps searchSteps;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void setUpIntegrationTest() {
        log.info("🚀 Setting up integration test environment...");

        containerManager.initializeContainers();

        if (!containerManager.areAllContainersAccessible()) {
            throw new IllegalStateException("❌ Containers are not accessible. Tests cannot proceed.");
        }
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @AfterEach
    void cleanupAfterTest() {
        cleanupAllTestData();
    }

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

        registry.add("search.embedding.providers.ollama.base-url", () ->
            "http://" + containerManager.getOllamaHost() + ":" + containerManager.getOllamaPort());
    }

    protected void cleanupAllTestData() {
        try {
            // Disable foreign key checks temporarily for truncation
            jdbcTemplate.execute("SET session_replication_role = 'replica'");

            // Truncate all tables (add more as schema grows)
            jdbcTemplate.execute("TRUNCATE TABLE note_links CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE note_embeddings CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE notes CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE directories CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");

            // Re-enable foreign key checks
            jdbcTemplate.execute("SET session_replication_role = 'origin'");
        } catch (Exception e) {
            log.error("⚠️  Error during test data cleanup: {}", e.getMessage(), e);
            throw e;
        }
    }
}
