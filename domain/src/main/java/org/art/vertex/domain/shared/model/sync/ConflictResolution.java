package org.art.vertex.domain.shared.model.sync;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.Note;

@Value
@Builder
public class ConflictResolution {

    Note resolvedNote;

    ResolutionStrategy strategy;

    String reason;
}