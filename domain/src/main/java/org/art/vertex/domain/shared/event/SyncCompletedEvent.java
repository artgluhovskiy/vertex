package org.art.vertex.domain.shared.event;

import lombok.Value;
import org.art.vertex.domain.shared.event.DomainEvent;
import org.art.vertex.domain.shared.model.sync.SyncResult;

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