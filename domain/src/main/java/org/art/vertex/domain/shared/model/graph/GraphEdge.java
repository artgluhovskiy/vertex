package org.art.vertex.domain.shared.model.graph;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.LinkType;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class GraphEdge {

    UUID id;

    UUID sourceId;

    UUID targetId;

    LinkType type;

    double weight;

    @Builder.Default
    Map<String, Object> properties = Map.of();
}