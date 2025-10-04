package org.art.vertex.domain.event.note;

import lombok.Value;
import org.art.vertex.domain.event.DomainEvent;
import org.art.vertex.domain.model.note.LinkType;

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