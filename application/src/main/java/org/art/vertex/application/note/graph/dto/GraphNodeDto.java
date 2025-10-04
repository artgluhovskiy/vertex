package org.art.vertex.application.note.graph.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class GraphNodeDto {
    UUID id;
    String label;
    String type;
}