package org.art.vertex.application.directory.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RenameDirectoryCommand {
    String newName;
}