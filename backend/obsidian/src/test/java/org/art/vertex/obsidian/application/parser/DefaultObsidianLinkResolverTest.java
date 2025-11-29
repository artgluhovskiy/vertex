package org.art.vertex.obsidian.application.parser;

import org.art.vertex.obsidian.domain.model.WikilinkReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultObsidianLinkResolver Unit Tests")
class DefaultObsidianLinkResolverTest {

    private DefaultObsidianLinkResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new DefaultObsidianLinkResolver();
    }

    @Test
    @DisplayName("Should resolve exact match wikilinks")
    void shouldResolveExactMatchWikilinks() {
        // Given
        var sourceId = UUID.randomUUID();
        var targetId = UUID.randomUUID();

        var wikilinks = Map.of(
            sourceId, List.of(
                WikilinkReference.builder()
                    .targetNoteName("Target Note")
                    .isEmbedded(false)
                    .lineNumber(1)
                    .build()
            )
        );

        var noteNameToId = Map.of("Target Note", targetId);

        // When
        var result = resolver.resolveLinks(wikilinks, noteNameToId);

        // Then
        assertThat(result).containsEntry(sourceId, List.of(targetId));
    }

    @Test
    @DisplayName("Should resolve case-insensitive wikilinks")
    void shouldResolveCaseInsensitiveWikilinks() {
        // Given
        var sourceId = UUID.randomUUID();
        var targetId = UUID.randomUUID();

        var wikilinks = Map.of(
            sourceId, List.of(
                WikilinkReference.builder()
                    .targetNoteName("target note")
                    .isEmbedded(false)
                    .lineNumber(1)
                    .build()
            )
        );

        var noteNameToId = Map.of("Target Note", targetId);

        // When
        var result = resolver.resolveLinks(wikilinks, noteNameToId);

        // Then
        assertThat(result).containsEntry(sourceId, List.of(targetId));
    }

    @Test
    @DisplayName("Should skip embedded references")
    void shouldSkipEmbeddedReferences() {
        // Given
        var sourceId = UUID.randomUUID();
        var targetId = UUID.randomUUID();

        var wikilinks = Map.of(
            sourceId, List.of(
                WikilinkReference.builder()
                    .targetNoteName("Image")
                    .isEmbedded(true)
                    .lineNumber(1)
                    .build()
            )
        );

        var noteNameToId = Map.of("Image", targetId);

        // When
        var result = resolver.resolveLinks(wikilinks, noteNameToId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle unresolved wikilinks gracefully")
    void shouldHandleUnresolvedWikilinks() {
        // Given
        var sourceId = UUID.randomUUID();

        var wikilinks = Map.of(
            sourceId, List.of(
                WikilinkReference.builder()
                    .targetNoteName("Non Existent")
                    .isEmbedded(false)
                    .lineNumber(1)
                    .build()
            )
        );

        var noteNameToId = Map.<String, UUID>of();

        // When
        var result = resolver.resolveLinks(wikilinks, noteNameToId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should resolve multiple wikilinks from same note")
    void shouldResolveMultipleWikilinks() {
        // Given
        var sourceId = UUID.randomUUID();
        var targetId1 = UUID.randomUUID();
        var targetId2 = UUID.randomUUID();

        var wikilinks = Map.of(
            sourceId, List.of(
                WikilinkReference.builder()
                    .targetNoteName("Note 1")
                    .isEmbedded(false)
                    .lineNumber(1)
                    .build(),
                WikilinkReference.builder()
                    .targetNoteName("Note 2")
                    .isEmbedded(false)
                    .lineNumber(2)
                    .build()
            )
        );

        var noteNameToId = Map.of(
            "Note 1", targetId1,
            "Note 2", targetId2
        );

        // When
        var result = resolver.resolveLinks(wikilinks, noteNameToId);

        // Then
        assertThat(result).containsEntry(sourceId, List.of(targetId1, targetId2));
    }

    @Test
    @DisplayName("Should handle wikilinks without .md extension")
    void shouldHandleWikilinksWithoutExtension() {
        // Given
        var sourceId = UUID.randomUUID();
        var targetId = UUID.randomUUID();

        var wikilinks = Map.of(
            sourceId, List.of(
                WikilinkReference.builder()
                    .targetNoteName("Note")
                    .isEmbedded(false)
                    .lineNumber(1)
                    .build()
            )
        );

        var noteNameToId = Map.of("Note.md", targetId);

        // When
        var result = resolver.resolveLinks(wikilinks, noteNameToId);

        // Then
        assertThat(result).containsEntry(sourceId, List.of(targetId));
    }

    @Test
    @DisplayName("Should handle large number of notes efficiently")
    void shouldHandleLargeVaultEfficiently() {
        // Given
        var sourceId = UUID.randomUUID();

        // Create 1000 notes
        var noteNameToId = new java.util.HashMap<String, UUID>();
        for (int i = 0; i < 1000; i++) {
            noteNameToId.put("Note " + i, UUID.randomUUID());
        }

        var targetId = noteNameToId.get("Note 500");

        var wikilinks = Map.of(
            sourceId, List.of(
                WikilinkReference.builder()
                    .targetNoteName("note 500") // lowercase
                    .isEmbedded(false)
                    .lineNumber(1)
                    .build()
            )
        );

        // When
        long startTime = System.nanoTime();
        var result = resolver.resolveLinks(wikilinks, noteNameToId);
        long duration = System.nanoTime() - startTime;

        // Then
        assertThat(result).containsEntry(sourceId, List.of(targetId));
        assertThat(duration).isLessThan(50_000_000); // Less than 50ms (reasonable for O(n) algorithm)
    }
}
