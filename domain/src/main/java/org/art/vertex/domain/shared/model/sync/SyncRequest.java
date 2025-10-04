package org.art.vertex.domain.shared.model.sync;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.Note;
import org.art.vertex.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class SyncRequest {

    User user;

    @Builder.Default
    List<Note> localNotes = List.of();

    LocalDateTime lastSyncTime;

    SyncDirection direction;
}