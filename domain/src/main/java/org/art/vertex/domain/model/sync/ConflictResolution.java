package org.art.vertex.domain.model.sync;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.model.note.Note;

@Value
@Builder
public class ConflictResolution {

    Note resolvedNote;

    ResolutionStrategy strategy;

    String reason;
}