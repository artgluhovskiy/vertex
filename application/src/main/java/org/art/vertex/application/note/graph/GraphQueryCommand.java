package org.art.vertex.application.note.graph;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.graph.model.TraversalStrategy;

import java.util.UUID;

@Value
@Builder
public class GraphQueryCommand {
    UUID noteId;
    UUID userId;
    int depth;
    TraversalStrategy strategy;
}