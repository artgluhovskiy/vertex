package org.art.vertex.domain.model.note;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class NoteFullTextSearch {

    UUID id;

    Note note;

    String searchableText;

    @Builder.Default
    Map<String, Object> searchMetadata = Map.of();

    LocalDateTime createdTs;

    LocalDateTime updatedTs;

    public static NoteFullTextSearch create(
        UUID id,
        Note note,
        String searchableText,
        Map<String, Object> searchMetadata,
        LocalDateTime ts
    ) {
        return NoteFullTextSearch.builder()
            .id(id)
            .note(note)
            .searchableText(searchableText)
            .searchMetadata(searchMetadata)
            .createdTs(ts)
            .updatedTs(ts)
            .build();
    }
}