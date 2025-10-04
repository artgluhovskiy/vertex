package org.art.vertex.application.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class GraphDto {
    List<GraphNodeDto> nodes;
    List<GraphEdgeDto> edges;
    Map<String, Object> metadata;
}