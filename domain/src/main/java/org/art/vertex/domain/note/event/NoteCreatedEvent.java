package org.art.vertex.domain.note.event;

import lombok.Value;
import org.art.vertex.domain.shared.event.DomainEvent;
import org.art.vertex.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class NoteCreatedEvent implements DomainEvent {

    UUID noteId;
    User user;
    String title;
    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return noteId;
    }

    @Override
    public String getEventType() {
        return "NOTE_CREATED";
    }
}