package org.art.vertex.application.directory.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UpdateDirectoryCommand {
    UUID parentId;
    String name;
}