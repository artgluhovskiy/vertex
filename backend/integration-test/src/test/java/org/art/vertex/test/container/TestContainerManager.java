package org.art.vertex.test.container;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestContainerManager {

    private static final TestContainerManager INSTANCE = new TestContainerManager();

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private static final int POSTGRES_PORT = 5432;

    private static final String POSTGRES_HOST = "localhost";

    private GenericContainer<?> postgresContainer;

    private boolean isExternalPostgres = false;

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

            checkExternalContainers();

            if (!isExternalPostgres) {
                startTestcontainers();
            }

            verifyContainersReady();

            log.info("✅ Test containers initialized successfully");

            Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
        }
    }

    public boolean areAllContainersAccessible() {
        return isPortOpen(getPostgresHost(), getPostgresPort());
    }

    private void checkExternalContainers() {
        log.info("🔍 Checking for external containers...");

        isExternalPostgres = isPortOpen(POSTGRES_HOST, POSTGRES_PORT);

        if (isExternalPostgres) {
            log.info("✅ External PostgreSQL container detected on port {}", POSTGRES_PORT);
        } else {
            log.info("ℹ️  No external PostgreSQL container found on port {}", POSTGRES_PORT);
        }
    }

    private void startTestcontainers() {
        log.info("🚀 Starting Testcontainers for missing services...");

        if (!isExternalPostgres) {
            startPostgresContainer();
        }
    }

    private void verifyContainersReady() {
        log.info("🔍 Verifying containers are ready...");

        if (isExternalPostgres) {
            verifyPostgresReady();
        } else if (postgresContainer != null) {
            verifyPostgresContainerReady();
        }

        log.info("✅ All containers are ready");
    }

    private boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

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

    // ------ PostgresSQL ------ //

    public int getPostgresPort() {
        if (isExternalPostgres) {
            return POSTGRES_PORT;
        } else if (postgresContainer != null) {
            return postgresContainer.getMappedPort(POSTGRES_PORT);
        } else {
            throw new IllegalStateException("PostgreSQL container not initialized");
        }
    }

    public String getPostgresHost() {
        return POSTGRES_HOST;
    }

    private void startPostgresContainer() {
        log.info("📦 Starting PostgreSQL container with Testcontainers...");

        postgresContainer = new GenericContainer<>(DockerImageName.parse("pgvector/pgvector:pg16"))
            .withExposedPorts(POSTGRES_PORT)
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

    private void verifyPostgresReady() {
        try {
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

    private void verifyPostgresContainerReady() {
        if (postgresContainer != null && postgresContainer.isRunning()) {
            log.info("✅ Testcontainers PostgreSQL is ready on port {}", postgresContainer.getMappedPort(5432));
        } else {
            throw new RuntimeException("Testcontainers PostgreSQL failed to start");
        }
    }
}
