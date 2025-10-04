package org.art.vertex.application.command;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.model.graph.TraversalStrategy;

import java.util.UUID;

@Value
@Builder
public class GraphQueryCommand {
    UUID noteId;
    UUID userId;
    int depth;
    TraversalStrategy strategy;
}