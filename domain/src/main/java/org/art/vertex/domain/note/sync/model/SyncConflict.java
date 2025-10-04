package org.art.vertex.domain.note.sync.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.model.Note;

import java.time.LocalDateTime;

@Value
@Builder
public class SyncConflict {

    Note localVersion;

    Note remoteVersion;

    ConflictType type;

    LocalDateTime detectedAt;
}