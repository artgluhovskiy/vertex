package org.art.vertex.infrastructure.note.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for semantic search functionality.
 * <p>
 * This class holds all configuration for:
 * - Embedding providers (Ollama, OpenAI, etc.)
 * - Text indexing strategies
 * - Search performance thresholds
 * - Vector search parameters
 * <p>
 * Configuration in application.yml:
 * <pre>
 * search:
 *   embedding:
 *     providers:
 *       ollama:
 *         enabled: true
 *         base-url: http://localhost:11434
 *         timeout: 30s
 *     default-model: OLLAMA_NOMIC_EMBED_TEXT_SMALL
 *   indexing:
 *     strategy: BASIC
 *     enhanced-enabled: false
 *   vector:
 *     default-limit: 20
 *     min-similarity: 0.7
 *     max-results: 100
 * </pre>
 *
 * @see EmbeddingProviderProperties
 * @see IndexingProperties
 * @see VectorSearchProperties
 */
@Data
@ConfigurationProperties(prefix = "search")
public class SearchProperties {

    /**
     * Embedding provider configuration.
     */
    private EmbeddingProperties embedding = new EmbeddingProperties();

    /**
     * Text indexing configuration.
     */
    private IndexingProperties indexing = new IndexingProperties();

    /**
     * Vector search configuration.
     */
    private VectorSearchProperties vector = new VectorSearchProperties();

    /**
     * Embedding provider configuration.
     */
    @Data
    public static class EmbeddingProperties {

        /**
         * Provider-specific configurations.
         */
        private Map<String, ProviderConfig> providers = new HashMap<>();

        /**
         * Default embedding model to use.
         * Must match one of the configured provider models.
         */
        private String defaultModel = "OLLAMA_NOMIC_EMBED_TEXT_SMALL";

        /**
         * Configuration for a specific embedding provider.
         */
        @Data
        public static class ProviderConfig {
            /**
             * Whether this provider is enabled.
             */
            private boolean enabled = true;

            /**
             * Base URL for the provider API.
             */
            private String baseUrl;

            /**
             * Request timeout duration.
             */
            private Duration timeout = Duration.ofSeconds(30);

            /**
             * API key for authenticated providers (OpenAI, etc.).
             */
            private String apiKey;

            /**
             * Additional provider-specific settings.
             */
            private Map<String, String> settings = new HashMap<>();
        }
    }

    /**
     * Text indexing strategy configuration.
     */
    @Data
    public static class IndexingProperties {

        /**
         * Default indexing strategy to use (BASIC, ENHANCED).
         */
        private String strategy = "BASIC";

        /**
         * Whether enhanced indexing strategy is enabled.
         */
        private boolean enhancedEnabled = false;

        /**
         * Whether to enable chunking for long notes.
         */
        private boolean chunkingEnabled = false;

        /**
         * Chunk size in characters (for chunked strategy).
         */
        private int chunkSize = 4000;

        /**
         * Chunk overlap in characters (for context continuity).
         */
        private int chunkOverlap = 200;
    }

    /**
     * Vector search configuration.
     */
    @Data
    public static class VectorSearchProperties {

        /**
         * Default number of results to return.
         */
        private int defaultLimit = 20;

        /**
         * Minimum similarity threshold (0.0 to 1.0).
         * Results below this threshold are filtered out.
         */
        private double minSimilarity = 0.7;

        /**
         * Maximum number of results that can be requested.
         */
        private int maxResults = 100;

        /**
         * Whether to include note content in search results.
         */
        private boolean includeContent = true;

        /**
         * Whether to enable query caching.
         */
        private boolean cacheEnabled = false;

        /**
         * Cache TTL duration.
         */
        private Duration cacheTtl = Duration.ofMinutes(15);
    }
}
