package org.art.vertex.domain.note.enrichment.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.model.NoteLink;
import org.art.vertex.domain.tag.model.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class EnrichmentResult {

    UUID noteId;

    EnrichmentType type;

    String summary;

    @Builder.Default
    List<Tag> suggestedTags = List.of();

    @Builder.Default
    List<NoteLink> suggestedLinks = List.of();

    @Builder.Default
    List<String> keywords = List.of();

    @Builder.Default
    Map<String, Object> metadata = Map.of();

    LocalDateTime processedAt;

    public static EnrichmentResult from(EnrichmentContext context, LocalDateTime processedAt) {
        return EnrichmentResult.builder()
            .processedAt(processedAt)
            .metadata(context.getParameters())
            .build();
    }
}