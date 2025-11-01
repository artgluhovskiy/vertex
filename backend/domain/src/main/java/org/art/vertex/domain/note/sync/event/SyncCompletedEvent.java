package org.art.vertex.domain.note.sync.event;

import lombok.Value;
import org.art.vertex.domain.note.sync.model.SyncResult;
import org.art.vertex.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class SyncCompletedEvent implements DomainEvent {

    UUID userId;
    SyncResult result;
    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return userId;
    }

    @Override
    public String getEventType() {
        return "SYNC_COMPLETED";
    }
}