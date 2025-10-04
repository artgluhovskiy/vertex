package org.art.vertex.domain.shared.model.enrichment;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.NoteLink;
import org.art.vertex.domain.tag.Tag;

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

    public static EnrichmentResult from(EnrichmentContext context) {
        return EnrichmentResult.builder()
            .processedAt(LocalDateTime.now())
            .metadata(context.getParameters())
            .build();
    }
}