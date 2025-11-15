package org.art.vertex.infrastructure.note.config;

import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.note.NoteEmbeddingRepository;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.search.VectorSearchService;
import org.art.vertex.domain.note.search.model.EmbeddingModel;
import org.art.vertex.domain.note.search.model.SearchConfiguration;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.tag.TagRepository;
import org.art.vertex.infrastructure.note.DefaultNoteRepository;
import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.art.vertex.infrastructure.note.jpa.NoteJpaRepository;
import org.art.vertex.infrastructure.note.mapper.NoteEntityMapper;
import org.art.vertex.infrastructure.note.search.DefaultNoteEmbeddingRepository;
import org.art.vertex.infrastructure.note.search.config.SearchProperties;
import org.art.vertex.infrastructure.note.search.embedding.EmbeddingProvider;
import org.art.vertex.infrastructure.note.search.embedding.EmbeddingProviderFactory;
import org.art.vertex.infrastructure.note.search.embedding.ollama.OllamaNomicEmbedTextProvider;
import org.art.vertex.infrastructure.note.search.entity.NoteEmbeddingEntity;
import org.art.vertex.infrastructure.note.search.indexing.BasicTextIndexingStrategy;
import org.art.vertex.infrastructure.note.search.indexing.EnhancedTextIndexingStrategy;
import org.art.vertex.infrastructure.note.search.indexing.TextIndexingStrategy;
import org.art.vertex.infrastructure.note.search.jpa.NoteEmbeddingJpaRepository;
import org.art.vertex.infrastructure.note.search.mapper.NoteEmbeddingEntityMapper;
import org.art.vertex.infrastructure.note.search.service.DefaultVectorSearchService;
import org.art.vertex.infrastructure.note.updater.NoteUpdater;
import org.art.vertex.infrastructure.tag.DefaultTagRepository;
import org.art.vertex.infrastructure.tag.entity.TagEntity;
import org.art.vertex.infrastructure.tag.jpa.TagJpaRepository;
import org.art.vertex.infrastructure.tag.mapper.TagEntityMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Duration;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackageClasses = {
    NoteJpaRepository.class,
    TagJpaRepository.class,
    NoteEmbeddingJpaRepository.class
})
@EntityScan(basePackageClasses = {
    NoteEntity.class,
    TagEntity.class,
    NoteEmbeddingEntity.class
})
@EnableConfigurationProperties(SearchProperties.class)
public class NoteInfrastructureConfig {

    @Bean
    public TagEntityMapper tagEntityMapper() {
        return new TagEntityMapper();
    }

    @Bean
    public TagRepository tagRepository(
        TagJpaRepository tagJpaRepository,
        TagEntityMapper tagEntityMapper
    ) {
        return new DefaultTagRepository(tagJpaRepository, tagEntityMapper);
    }

    @Bean
    public NoteEntityMapper noteEntityMapper(
        DirectoryRepository directoryRepository,
        TagEntityMapper tagEntityMapper
    ) {
        return new NoteEntityMapper(directoryRepository, tagEntityMapper);
    }

    @Bean
    public NoteUpdater noteUpdater(TagEntityMapper tagEntityMapper) {
        return new NoteUpdater(tagEntityMapper);
    }

    @Bean
    public NoteRepository noteRepository(
        NoteJpaRepository noteJpaRepository,
        NoteEntityMapper noteEntityMapper,
        NoteUpdater noteUpdater
    ) {
        return new DefaultNoteRepository(noteJpaRepository, noteEntityMapper, noteUpdater);
    }

    // ========== Text Indexing Strategies ==========

    @Bean
    public TextIndexingStrategy basicTextIndexingStrategy() {
        return new BasicTextIndexingStrategy();
    }

    @Bean
    @ConditionalOnProperty(
        name = "search.indexing.enhanced-enabled",
        havingValue = "true",
        matchIfMissing = false
    )
    public TextIndexingStrategy enhancedTextIndexingStrategy() {
        return new EnhancedTextIndexingStrategy();
    }

    // ========== Embedding Providers ==========

    @Bean
    @ConditionalOnProperty(
        name = "search.embedding.providers.ollama.enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    public EmbeddingProvider ollamaNomicEmbedTextProvider(SearchProperties searchProperties) {
        SearchProperties.EmbeddingProperties.ProviderConfig ollamaConfig =
            searchProperties.getEmbedding().getProviders().get("ollama");

        String baseUrl = ollamaConfig != null && ollamaConfig.getBaseUrl() != null
            ? ollamaConfig.getBaseUrl()
            : "http://localhost:11434";

        Duration timeout = ollamaConfig != null && ollamaConfig.getTimeout() != null
            ? ollamaConfig.getTimeout()
            : Duration.ofSeconds(30);

        return new OllamaNomicEmbedTextProvider(baseUrl, timeout);
    }

    @Bean
    public EmbeddingProviderFactory embeddingProviderFactory(List<EmbeddingProvider> providers) {
        return new EmbeddingProviderFactory(providers);
    }

    // ========== Search Configuration ==========

    @Bean
    public SearchConfiguration searchConfiguration(SearchProperties searchProperties) {
        SearchProperties.VectorSearchProperties vectorProps = searchProperties.getVector();

        return SearchConfiguration.builder()
            .vectorMinSimilarityThreshold(vectorProps.getMinSimilarity())
            .defaultMaxResults(vectorProps.getDefaultLimit())
            .build();
    }

    // ========== Embedding Repository ==========

    @Bean
    public NoteEmbeddingEntityMapper noteEmbeddingEntityMapper() {
        return new NoteEmbeddingEntityMapper();
    }

    @Bean
    public NoteEmbeddingRepository noteEmbeddingRepository(
        NoteEmbeddingJpaRepository jpaRepository,
        NoteEmbeddingEntityMapper mapper
    ) {
        return new DefaultNoteEmbeddingRepository(jpaRepository, mapper);
    }

    // ========== Vector Search Service ==========

    @Bean
    public VectorSearchService vectorSearchService(
        NoteEmbeddingRepository embeddingRepository,
        EmbeddingProviderFactory providerFactory,
        TextIndexingStrategy indexingStrategy,
        SearchConfiguration searchConfiguration,
        UuidGenerator uuidGenerator,
        Clock clock,
        SearchProperties searchProperties
    ) {
        // Get default model from configuration
        String defaultModelName = searchProperties.getEmbedding().getDefaultModel();
        EmbeddingModel defaultModel = EmbeddingModel.valueOf(defaultModelName);

        return new DefaultVectorSearchService(
            embeddingRepository,
            providerFactory,
            indexingStrategy,
            searchConfiguration,
            uuidGenerator,
            clock,
            defaultModel
        );
    }
}
