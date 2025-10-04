package org.art.vertex.domain.note.graph.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.model.LinkType;

import java.util.Set;

@Value
@Builder
public class GraphViewConfig {

    int maxDepth;

    int maxNodes;

    boolean includeSemanticLinks;

    double semanticThreshold;

    @Builder.Default
    Set<LinkType> linkTypes = Set.of();
}