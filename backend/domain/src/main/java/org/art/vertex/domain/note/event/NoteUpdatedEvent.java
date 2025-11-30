package org.art.vertex.domain.note.event;

import lombok.Value;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class NoteUpdatedEvent implements DomainEvent {

    Note note;

    String previousTitle;

    String previousContent;

    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return note.getId();
    }

    @Override
    public String getEventType() {
        return "NOTE_UPDATED";
    }
}