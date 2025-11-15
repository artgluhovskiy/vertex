package org.art.vertex.infrastructure.note.search.embedding;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.search.model.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory for obtaining embedding provider instances.
 * Receives all {@link EmbeddingProvider} instances via constructor injection
 * and maps them by their supported {@link EmbeddingModel}.
 * <p>
 * This factory pattern enables:
 * - Runtime provider selection based on configuration
 * - Centralized provider management
 * - Health checking before provider usage
 * - Easy addition of new providers (register in NoteInfrastructureConfig)
 * <p>
 * Example usage:
 * <pre>
 * EmbeddingProvider provider = factory.getProvider(EmbeddingModel.OLLAMA_NOMIC_EMBED_TEXT_SMALL);
 * if (provider.isReady()) {
 *     List&lt;Float&gt; embedding = provider.embed("Hello world");
 * }
 * </pre>
 */
@Slf4j
public class EmbeddingProviderFactory {

    private final Map<EmbeddingModel, EmbeddingProvider> providers;

    /**
     * Constructor with explicit provider list.
     * Providers are registered in NoteInfrastructureConfig.
     *
     * @param providerList List of all embedding provider instances
     */
    public EmbeddingProviderFactory(List<EmbeddingProvider> providerList) {
        this.providers = providerList.stream()
            .collect(Collectors.toMap(
                EmbeddingProvider::getSupportedModel,
                Function.identity(),
                (existing, replacement) -> {
                    log.warn("Duplicate provider for model: {}. Using first registered: {}",
                        existing.getSupportedModel(),
                        existing.getClass().getSimpleName());
                    return existing;
                }
            ));

        log.info("Initialized EmbeddingProviderFactory with {} providers: {}",
            providers.size(),
            providers.keySet().stream()
                .map(model -> String.format("%s (%dd)", model, providers.get(model).getDimension()))
                .collect(Collectors.joining(", ")));

        // Log readiness status
        providers.forEach((model, provider) -> {
            boolean ready = provider.isReady();
            if (ready) {
                log.info("Provider {} is ready", model);
            } else {
                log.warn("Provider {} is NOT ready", model);
            }
        });
    }

    /**
     * Get embedding provider for specified model.
     *
     * @param model Embedding model enum
     * @return Embedding provider implementation
     * @throws IllegalArgumentException if no provider is registered for the model
     */
    public EmbeddingProvider getProvider(EmbeddingModel model) {
        EmbeddingProvider provider = providers.get(model);
        if (provider == null) {
            throw new IllegalArgumentException(
                String.format("No embedding provider registered for model: %s. " +
                        "Available models: %s",
                    model, getAvailableModels()));
        }
        return provider;
    }

    /**
     * Get all available embedding models.
     * Returns all models that have a registered provider.
     *
     * @return Set of available embedding models
     */
    public Set<EmbeddingModel> getAvailableModels() {
        return providers.keySet();
    }

    /**
     * Check if provider is available for specific model.
     *
     * @param model Embedding model to check
     * @return true if provider exists, false otherwise
     */
    public boolean hasProvider(EmbeddingModel model) {
        return providers.containsKey(model);
    }

    /**
     * Check if provider is available AND ready for specific model.
     *
     * @param model Embedding model to check
     * @return true if provider exists and is ready, false otherwise
     */
    public boolean isModelAvailable(EmbeddingModel model) {
        EmbeddingProvider provider = providers.get(model);
        return provider != null && provider.isReady();
    }

    /**
     * Get all ready providers.
     * Filters providers that pass health check.
     *
     * @return Map of ready providers (model → provider)
     */
    public Map<EmbeddingModel, EmbeddingProvider> getReadyProviders() {
        return providers.entrySet().stream()
            .filter(entry -> entry.getValue().isReady())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }

    /**
     * Get total number of registered providers.
     *
     * @return Provider count
     */
    public int getProviderCount() {
        return providers.size();
    }

    /**
     * Get number of ready providers.
     *
     * @return Ready provider count
     */
    public int getReadyProviderCount() {
        return (int) providers.values().stream()
            .filter(EmbeddingProvider::isReady)
            .count();
    }

    /**
     * Validate that at least one provider is available and ready.
     * Useful for startup checks.
     *
     * @throws IllegalStateException if no providers are ready
     */
    public void validateAtLeastOneProviderReady() {
        if (getReadyProviderCount() == 0) {
            throw new IllegalStateException(
                String.format("No embedding providers are ready. " +
                        "Registered providers: %s. " +
                        "Please check provider configuration and availability.",
                    getAvailableModels()));
        }
    }

    /**
     * Get provider status summary (for health check endpoints).
     *
     * @return Map of model name → ready status
     */
    public Map<String, Boolean> getProviderStatusSummary() {
        return providers.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().name(),
                entry -> entry.getValue().isReady()
            ));
    }
}
