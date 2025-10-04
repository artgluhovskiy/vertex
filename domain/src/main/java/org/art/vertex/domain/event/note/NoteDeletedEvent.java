package org.art.vertex.domain.event.note;

import lombok.Value;
import org.art.vertex.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class NoteDeletedEvent implements DomainEvent {

    UUID noteId;
    UUID userId;
    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return noteId;
    }

    @Override
    public String getEventType() {
        return "NOTE_DELETED";
    }
}