package org.art.vertex.domain.note.event;

import lombok.Value;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class NoteCreatedEvent implements DomainEvent {

    Note note;

    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return note.getId();
    }

    @Override
    public String getEventType() {
        return "NOTE_CREATED";
    }
}