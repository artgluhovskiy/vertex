package org.art.vertex.domain.model.sync;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.model.note.Note;

import java.time.LocalDateTime;

@Value
@Builder
public class SyncConflict {

    Note localVersion;

    Note remoteVersion;

    ConflictType type;

    LocalDateTime detectedAt;
}