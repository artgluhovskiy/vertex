package org.art.vertex.test.container;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Centralized container manager that automatically detects and manages test containers.
 * This class provides a unified interface for all integration tests and automatically
 * starts containers when they're not available.
 */
@Slf4j
public class TestContainerManager {

//    private static final Logger logger = LoggerFactory.getLogger(TestContainerManager.class);
    private static final TestContainerManager INSTANCE = new TestContainerManager();
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    // Container configuration
    private static final int POSTGRES_PORT = 5432;
    private static final String POSTGRES_HOST = "localhost";

    // Container instance
    private GenericContainer<?> postgresContainer;

    // Container status
    private boolean postgresExternal = false;

    private TestContainerManager() {
        // Private constructor for singleton
    }

    public static TestContainerManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initialize containers automatically. This method will:
     * 1. Check if external containers are available
     * 2. Start Testcontainers if external containers are not available
     * 3. Ensure all required containers are ready
     */
    public synchronized void initializeContainers() {
        if (INITIALIZED.compareAndSet(false, true)) {
            log.info("🚀 Initializing test containers...");

            // Check external containers first
            checkExternalContainers();

            // Start Testcontainers if external containers are not available
            if (!postgresExternal) {
                startTestcontainers();
            }

            // Verify all containers are ready
            verifyContainersReady();

            log.info("✅ Test containers initialized successfully");

            // Add shutdown hook for cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
        }
    }

    /**
     * Check if external containers are available.
     */
    private void checkExternalContainers() {
        log.info("🔍 Checking for external containers...");

        // Check PostgreSQL
        postgresExternal = isPortOpen(POSTGRES_HOST, POSTGRES_PORT);
        if (postgresExternal) {
            log.info("✅ External PostgreSQL container detected on port {}", POSTGRES_PORT);
        } else {
            log.info("ℹ️  No external PostgreSQL container found on port {}", POSTGRES_PORT);
        }
    }

    /**
     * Start Testcontainers for missing services.
     */
    private void startTestcontainers() {
        log.info("🚀 Starting Testcontainers for missing services...");

        // Start PostgreSQL if external is not available
        if (!postgresExternal) {
            startPostgresContainer();
        }
    }

    /**
     * Start PostgreSQL container using Testcontainers.
     */
    private void startPostgresContainer() {
        log.info("📦 Starting PostgreSQL container with Testcontainers...");

        postgresContainer = new GenericContainer<>(DockerImageName.parse("pgvector/pgvector:pg16"))
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", "vertex")
            .withEnv("POSTGRES_USER", "vertex")
            .withEnv("POSTGRES_PASSWORD", "vertex")
            .withStartupTimeout(Duration.ofMinutes(2))
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
            .waitingFor(Wait.forListeningPort())
            .withLabel("testcontainers.vertex", "postgres")
            .withLabel("testcontainers.vertex.purpose", "integration-test")
            .withReuse(true)
            .withLogConsumer(outputFrame -> {
                String logLine = outputFrame.getUtf8String();
                if (logLine.contains("database system is ready") ||
                    logLine.contains("listening on IPv4") ||
                    logLine.contains("pgvector")) {
                    log.info("[PostgreSQL] {}", logLine.trim());
                }
            });

        postgresContainer.start();
        log.info("✅ PostgreSQL container started on port {}", postgresContainer.getMappedPort(5432));
    }

    /**
     * Verify all containers are ready and accessible.
     */
    private void verifyContainersReady() {
        log.info("🔍 Verifying containers are ready...");

        // Verify PostgreSQL
        if (postgresExternal) {
            verifyPostgresReady();
        } else if (postgresContainer != null) {
            verifyPostgresContainerReady();
        }

        log.info("✅ All containers are ready");
    }

    /**
     * Verify external PostgreSQL is ready.
     */
    private void verifyPostgresReady() {
        try {
            // Wait for PostgreSQL to be ready
            int maxAttempts = 30;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                if (isPortOpen(POSTGRES_HOST, POSTGRES_PORT)) {
                    log.info("✅ External PostgreSQL is ready on port {}", POSTGRES_PORT);
                    return;
                }

                if (attempt == maxAttempts) {
                    throw new RuntimeException("External PostgreSQL failed to become ready");
                }

                log.debug("Waiting for external PostgreSQL... (attempt {}/{})", attempt, maxAttempts);
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for PostgreSQL", e);
        }
    }

    /**
     * Verify Testcontainers PostgreSQL is ready.
     */
    private void verifyPostgresContainerReady() {
        if (postgresContainer != null && postgresContainer.isRunning()) {
            log.info("✅ Testcontainers PostgreSQL is ready on port {}", postgresContainer.getMappedPort(5432));
        } else {
            throw new RuntimeException("Testcontainers PostgreSQL failed to start");
        }
    }

    /**
     * Get the PostgreSQL port (external or Testcontainers).
     */
    public int getPostgresPort() {
        if (postgresExternal) {
            return POSTGRES_PORT;
        } else if (postgresContainer != null) {
            return postgresContainer.getMappedPort(5432);
        } else {
            throw new IllegalStateException("PostgreSQL container not initialized");
        }
    }

    /**
     * Get the PostgreSQL host.
     */
    public String getPostgresHost() {
        return POSTGRES_HOST;
    }

    /**
     * Check if all containers are accessible.
     */
    public boolean areAllContainersAccessible() {
        return isPortOpen(getPostgresHost(), getPostgresPort());
    }

    /**
     * Get container status information.
     */
    public String getContainerStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Container Status:\n");

        // PostgreSQL status
        if (postgresExternal) {
            status.append("  PostgreSQL: ✅ External (Port: ").append(getPostgresPort()).append(")\n");
        } else if (postgresContainer != null) {
            status.append("  PostgreSQL: ✅ Testcontainers (Port: ").append(getPostgresPort()).append(")\n");
        } else {
            status.append("  PostgreSQL: ❌ Not Available\n");
        }

        return status.toString();
    }

    /**
     * Check if containers are using external services.
     */
    public boolean isUsingExternalContainers() {
        return postgresExternal;
    }

    /**
     * Check if a port is open on a host.
     */
    private boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Cleanup resources.
     */
    private void cleanup() {
        log.info("🛑 Cleaning up test containers...");

        try {
            if (postgresContainer != null && postgresContainer.isRunning()) {
                postgresContainer.stop();
            }
            log.info("✅ Test containers cleaned up");
        } catch (Exception e) {
            log.error("⚠️  Error during cleanup: {}", e.getMessage());
        }
    }
}
