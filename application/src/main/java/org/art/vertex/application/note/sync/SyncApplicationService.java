package org.art.vertex.application.note.sync;

import org.art.vertex.application.note.sync.command.SyncCommand;
import org.art.vertex.application.note.sync.dto.SyncResultDto;

import java.util.UUID;

public interface SyncApplicationService {

    SyncResultDto sync(SyncCommand command);

    SyncResultDto syncUser(UUID userId);

    SyncResultDto resolveConflicts(UUID userId);
}