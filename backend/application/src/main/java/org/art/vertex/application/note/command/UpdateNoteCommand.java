package org.art.vertex.application.note.command;

import lombok.Builder;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class UpdateNoteCommand {

    String title;

    String content;

    UUID dirId;

    @Builder.Default
    Set<String> tags = Set.of();
}