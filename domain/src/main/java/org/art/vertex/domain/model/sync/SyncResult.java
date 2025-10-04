package org.art.vertex.domain.model.sync;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.model.note.Note;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class SyncResult {

    @Builder.Default
    List<Note> syncedNotes = List.of();

    @Builder.Default
    List<SyncConflict> conflicts = List.of();

    int totalProcessed;

    int successCount;

    int failureCount;

    LocalDateTime syncedAt;

    @Builder.Default
    Map<String, Object> metadata = Map.of();
}