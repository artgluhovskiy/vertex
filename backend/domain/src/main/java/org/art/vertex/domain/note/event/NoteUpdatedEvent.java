package org.art.vertex.domain.note.event;

import lombok.Value;
import org.art.vertex.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class NoteUpdatedEvent implements DomainEvent {

    UUID noteId;
    String previousTitle;
    String newTitle;
    String previousContent;
    String newContent;
    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return noteId;
    }

    @Override
    public String getEventType() {
        return "NOTE_UPDATED";
    }
}