package org.art.vertex.domain.shared.model.sync;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.Note;

import java.time.LocalDateTime;

@Value
@Builder
public class SyncConflict {

    Note localVersion;

    Note remoteVersion;

    ConflictType type;

    LocalDateTime detectedAt;
}