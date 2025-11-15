package org.art.vertex.infrastructure.embedding;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.search.model.EmbeddingModel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class EmbeddingProviderFactory {

    private final Map<EmbeddingModel, EmbeddingProvider> providers;

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

        providers.forEach((model, provider) -> {
            boolean ready = provider.isReady();
            if (ready) {
                log.info("Provider {} is ready", model);
            } else {
                log.warn("Provider {} is NOT ready", model);
            }
        });
    }

    public EmbeddingProvider getProvider(EmbeddingModel model) {
        EmbeddingProvider provider = providers.get(model);
        if (provider == null) {
            throw new IllegalArgumentException(
                String.format("No embedding provider registered for model: %s. Available models: %s",
                    model, getAvailableModels()));
        }
        return provider;
    }

    public Set<EmbeddingModel> getAvailableModels() {
        return providers.keySet();
    }
}
