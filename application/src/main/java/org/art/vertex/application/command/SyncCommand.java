package org.art.vertex.application.command;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.model.sync.SyncDirection;

import java.util.UUID;

@Value
@Builder
public class SyncCommand {
    UUID userId;
    SyncDirection direction;
}