package org.art.vertex.application.note.command;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class UpdateNoteCommand {
    String title;
    String content;
    UUID dirId;
    List<String> tags;
}