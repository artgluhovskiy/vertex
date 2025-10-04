package org.art.vertex.application.tag.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateTagCommand {
    String name;
    String description;
}