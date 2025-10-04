package org.art.vertex.domain.event.tag;

import lombok.Value;
import org.art.vertex.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class TagCreatedEvent implements DomainEvent {

    UUID tagId;
    UUID userId;
    String tagName;
    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return tagId;
    }

    @Override
    public String getEventType() {
        return "TAG_CREATED";
    }
}