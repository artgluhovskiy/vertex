package org.art.vertex.application.dto;

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