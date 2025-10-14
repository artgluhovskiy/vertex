package org.art.vertex.application.note.command;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class CreateNoteCommand {

    String title;

    String content;

    UUID dirId;

    // Tags created by the user manually
    List<String> tags;

    Map<String, Object> metadata;
}