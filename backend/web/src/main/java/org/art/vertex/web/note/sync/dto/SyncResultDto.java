package org.art.vertex.web.note.sync.dto;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.web.note.dto.NoteDto;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class SyncResultDto {
    List<NoteDto> syncedNotes;
    int totalProcessed;
    int successCount;
    int failureCount;
    int conflictCount;
    LocalDateTime syncedAt;
}
