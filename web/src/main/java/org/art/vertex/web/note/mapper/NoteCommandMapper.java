package org.art.vertex.web.note.mapper;

import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.web.note.request.CreateNoteRequest;
import org.art.vertex.web.note.request.UpdateNoteRequest;

public class NoteCommandMapper {

    public CreateNoteCommand toCommand(CreateNoteRequest request) {
        return CreateNoteCommand.builder()
            .dirId(request.dirId())
            .title(request.title())
            .content(request.content())
            .tags(request.tags())
            .build();
    }

    public UpdateNoteCommand toCommand(UpdateNoteRequest request) {
        return UpdateNoteCommand.builder()
            .title(request.title())
            .content(request.content())
            .dirId(request.dirId())
            .tags(request.tags())
            .build();
    }
}
