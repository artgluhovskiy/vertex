package org.art.vertex.application.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateTagCommand {
    String name;
    String description;
}