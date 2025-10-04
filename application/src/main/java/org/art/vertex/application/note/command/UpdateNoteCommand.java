package org.art.vertex.application.note.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateNoteCommand {
    String title;
    String content;
}