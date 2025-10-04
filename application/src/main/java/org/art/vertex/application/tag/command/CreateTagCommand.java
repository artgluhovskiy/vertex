package org.art.vertex.application.tag.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CreateTagCommand {
    UUID userId;
    String name;
    String description;
}