package org.art.vertex.domain.note.sync.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.user.model.User;

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