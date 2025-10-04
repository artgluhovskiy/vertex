package org.art.vertex.application.command;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CreateNoteCommand {
    UUID userId;
    UUID directoryId;
    String title;
    String content;
    List<UUID> tagIds;
}