package org.art.vertex.application.command;

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