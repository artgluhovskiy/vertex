package org.art.vertex.domain.note.event;

import lombok.Value;
import org.art.vertex.domain.shared.event.DomainEvent;
import org.art.vertex.domain.note.model.LinkType;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class NoteLinkCreatedEvent implements DomainEvent {

    UUID linkId;
    UUID sourceNoteId;
    UUID targetNoteId;
    LinkType linkType;
    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return linkId;
    }

    @Override
    public String getEventType() {
        return "NOTE_LINK_CREATED";
    }
}