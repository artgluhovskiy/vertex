package org.art.vertex.web.note.graph.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class GraphEdgeDto {
    UUID id;
    UUID sourceId;
    UUID targetId;
    String type;
    double weight;
}
