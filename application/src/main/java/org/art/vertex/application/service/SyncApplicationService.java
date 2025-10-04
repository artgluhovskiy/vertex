package org.art.vertex.application.service;

import org.art.vertex.application.command.SyncCommand;
import org.art.vertex.application.dto.SyncResultDto;

import java.util.UUID;

public interface SyncApplicationService {

    SyncResultDto sync(SyncCommand command);

    SyncResultDto syncUser(UUID userId);

    SyncResultDto resolveConflicts(UUID userId);
}