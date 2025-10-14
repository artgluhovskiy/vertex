package org.art.vertex.application.note.command;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CreateNoteCommand {
    UUID userId;
    UUID dirId;
    String title;
    String content;
    List<UUID> tagIds;
}