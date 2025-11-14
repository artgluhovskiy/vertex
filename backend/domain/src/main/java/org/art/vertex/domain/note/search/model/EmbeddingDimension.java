package org.art.vertex.domain.note.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmbeddingDimension {
    SMALL(768),
    MEDIUM(1024),
    LARGE(1536);

    private final int dim;
}
