package org.art.vertex.test.container;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.Container;
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
    private static final int OLLAMA_PORT = 11434;

    private static final String POSTGRES_HOST = "localhost";
    private static final String OLLAMA_HOST = "localhost";

    private GenericContainer<?> postgresContainer;
    private GenericContainer<?> ollamaContainer;

    private boolean isExternalPostgres = false;
    private boolean isExternalOllama = false;

    public static TestContainerManager getInstance() {
        return INSTANCE;
    }

    public synchronized void initializeContainers() {
        if (INITIALIZED.compareAndSet(false, true)) {
            log.info("🚀 Initializing test containers...");

            checkExternalContainers();

            startTestcontainers();

            verifyContainersReady();

            log.info("✅ Test containers initialized successfully");

            Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
        }
    }

    public boolean areAllContainersAccessible() {
        return isPortOpen(getPostgresHost(), getPostgresPort()) &&
            isPortOpen(getOllamaHost(), getOllamaPort());
    }

    private void checkExternalContainers() {
        log.info("🔍 Checking for external containers...");

        isExternalPostgres = isPortOpen(POSTGRES_HOST, POSTGRES_PORT);
        isExternalOllama = isPortOpen(OLLAMA_HOST, OLLAMA_PORT);

        if (isExternalPostgres) {
            log.info("✅ External PostgreSQL container detected on port {}", POSTGRES_PORT);
        } else {
            log.info("ℹ️  No external PostgreSQL container found on port {}", POSTGRES_PORT);
        }

        if (isExternalOllama) {
            log.info("✅ External Ollama container detected on port {}", OLLAMA_PORT);
        } else {
            log.info("ℹ️  No external Ollama container found on port {}", OLLAMA_PORT);
        }
    }

    private void startTestcontainers() {
        log.info("🚀 Starting Testcontainers for missing services...");

        if (!isExternalPostgres) {
            startPostgresContainer();
        }

        if (!isExternalOllama) {
            startOllamaContainer();
        }
    }

    private void verifyContainersReady() {
        log.info("🔍 Verifying containers are ready...");

        if (isExternalPostgres) {
            verifyPostgresReady();
        } else if (postgresContainer != null) {
            verifyPostgresContainerReady();
        }

        if (isExternalOllama) {
            verifyOllamaReady();
        } else if (ollamaContainer != null) {
            verifyOllamaContainerReady();
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
            if (ollamaContainer != null && ollamaContainer.isRunning()) {
                ollamaContainer.stop();
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

    // ------ Ollama ------ //

    public int getOllamaPort() {
        if (isExternalOllama) {
            return OLLAMA_PORT;
        } else if (ollamaContainer != null) {
            return ollamaContainer.getMappedPort(OLLAMA_PORT);
        } else {
            throw new IllegalStateException("Ollama container not initialized");
        }
    }

    public String getOllamaHost() {
        return OLLAMA_HOST;
    }

    private void startOllamaContainer() {
        log.info("📦 Starting Ollama container with Testcontainers...");

        ollamaContainer = new GenericContainer<>(DockerImageName.parse("ollama/ollama:latest"))
            .withExposedPorts(OLLAMA_PORT)
            .withStartupTimeout(Duration.ofMinutes(5))
            .waitingFor(Wait.forHttp("/api/tags").forPort(OLLAMA_PORT)
                .withStartupTimeout(Duration.ofMinutes(3)))
            .withLabel("testcontainers.vertex", "ollama")
            .withLabel("testcontainers.vertex.purpose", "integration-test")
            .withReuse(true)
            .withEnv("OLLAMA_INSECURE", "true")
            .withEnv("OLLAMA_SKIP_VERIFY", "true")
            .withEnv("OLLAMA_TLS_VERIFY", "false")
            .withEnv("OLLAMA_NOHISTORY", "true")
            .withEnv("CURL_CA_BUNDLE", "")
            .withLogConsumer(outputFrame -> {
                String logLine = outputFrame.getUtf8String();
                if (logLine.contains("Listening on") || logLine.contains("Starting") ||
                    logLine.contains("pulling") || logLine.contains("success")) {
                    log.info("[Ollama] {}", logLine.trim());
                }
            });

        ollamaContainer.start();
        log.info("✅ Ollama container started on port {}", ollamaContainer.getMappedPort(OLLAMA_PORT));

        log.info("🔄 Starting model pull process...");
        pullEmbeddingModel();
    }

    private void pullEmbeddingModel() {
        if (ollamaContainer == null || !ollamaContainer.isRunning()) {
            log.warn("⚠️  Cannot pull models: Ollama container not running");
            return;
        }

        String[] requiredModels = {"nomic-embed-text"};

        try {
            log.info("⏳ Waiting for Ollama to be fully ready before pulling models...");
            Thread.sleep(3000);

            for (String model : requiredModels) {
                log.info("📥 Pulling {} model in Ollama container...", model);

                Container.ExecResult result = ollamaContainer.execInContainer(
                    "sh", "-c",
                    String.format("OLLAMA_INSECURE=true OLLAMA_SKIP_VERIFY=true ollama pull %s", model)
                );

                if (result.getExitCode() == 0) {
                    log.info("✅ Successfully pulled {} model", model);
                    log.debug("Model pull output for {}: {}", model, result.getStdout());
                } else {
                    log.error("❌ Failed to pull {} model. Exit code: {}", model, result.getExitCode());
                    log.error("Error output: {}", result.getStderr());
                    log.error("Stdout output: {}", result.getStdout());
                    log.warn("⚠️  Continuing with other models despite {} failure", model);
                }

                Thread.sleep(1000);
            }

            log.info("📋 Listing available models after pull operations...");
            Container.ExecResult listResult = ollamaContainer.execInContainer(
                "sh", "-c", "ollama list"
            );

            if (listResult.getExitCode() == 0) {
                log.info("✅ Available models:\n{}", listResult.getStdout());
            } else {
                log.warn("⚠️  Could not list models: {}", listResult.getStderr());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("❌ Interrupted while waiting for Ollama", e);
        } catch (Exception e) {
            log.error("❌ Exception while pulling embedding models", e);
        }
    }

    private void verifyOllamaReady() {
        try {
            int maxAttempts = 30;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                if (isPortOpen(OLLAMA_HOST, OLLAMA_PORT)) {
                    log.info("✅ External Ollama is ready on port {}", OLLAMA_PORT);
                    return;
                }

                if (attempt == maxAttempts) {
                    throw new RuntimeException("External Ollama failed to become ready");
                }

                log.debug("Waiting for external Ollama... (attempt {}/{})", attempt, maxAttempts);
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Ollama", e);
        }
    }

    private void verifyOllamaContainerReady() {
        if (ollamaContainer != null && ollamaContainer.isRunning()) {
            log.info("✅ Testcontainers Ollama is ready on port {}", ollamaContainer.getMappedPort(OLLAMA_PORT));
        } else {
            throw new RuntimeException("Testcontainers Ollama failed to start");
        }
    }
}
