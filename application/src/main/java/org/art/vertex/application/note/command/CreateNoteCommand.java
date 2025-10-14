package org.art.vertex.application.note.command;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class CreateNoteCommand {

    String title;

    String content;

    UUID dirId;

    // Tags created by the user manually
    @Builder.Default
    Set<String> tags = Set.of();

    @Builder.Default
    Map<String, Object> metadata = Map.of();
}