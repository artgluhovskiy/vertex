package org.art.vertex.application.directory.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CreateDirectoryCommand {
    UUID userId;
    UUID parentId;
    String name;
}