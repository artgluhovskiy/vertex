package org.art.vertex.domain.note.sync.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.model.Note;

@Value
@Builder
public class ConflictResolution {

    Note resolvedNote;

    ResolutionStrategy strategy;

    String reason;
}