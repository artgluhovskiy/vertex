package org.art.vertex.application.tag.command;

import lombok.Builder;

@Builder
public record UpsertTagCommand(
    String name
) {
}