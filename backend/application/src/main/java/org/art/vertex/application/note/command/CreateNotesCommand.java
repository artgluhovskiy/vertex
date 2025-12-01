package org.art.vertex.application.note.command;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class CreateNotesCommand {

    @Builder.Default
    List<CreateNoteCommand> notes = new ArrayList<>();
}
