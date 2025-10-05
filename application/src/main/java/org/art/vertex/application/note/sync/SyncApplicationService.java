package org.art.vertex.application.note.sync;

import org.art.vertex.application.note.sync.command.SyncCommand;
import org.art.vertex.domain.note.sync.model.SyncResult;

import java.util.UUID;

public interface SyncApplicationService {

    SyncResult sync(SyncCommand command);

    SyncResult syncUser(UUID userId);

    SyncResult resolveConflicts(UUID userId);
}