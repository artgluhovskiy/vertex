package org.art.vertex.domain.note.event;

import lombok.Value;
import org.art.vertex.domain.shared.event.DomainEvent;
import org.art.vertex.domain.shared.model.enrichment.EnrichmentResult;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class NoteEnrichedEvent implements DomainEvent {

    UUID noteId;
    EnrichmentResult result;
    LocalDateTime occurredAt;

    @Override
    public UUID getAggregateId() {
        return noteId;
    }

    @Override
    public String getEventType() {
        return "NOTE_ENRICHED";
    }
}