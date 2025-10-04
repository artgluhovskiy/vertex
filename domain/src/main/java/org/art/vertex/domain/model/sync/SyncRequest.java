package org.art.vertex.domain.model.sync;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.model.note.Note;
import org.art.vertex.domain.model.user.User;

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