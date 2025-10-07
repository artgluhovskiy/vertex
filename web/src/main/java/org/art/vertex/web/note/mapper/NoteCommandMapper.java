package org.art.vertex.web.note.mapper;

import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.web.note.request.CreateNoteRequest;
import org.art.vertex.web.note.request.UpdateNoteRequest;

import java.util.UUID;

public class NoteCommandMapper {

    public CreateNoteCommand toCommand(CreateNoteRequest request, UUID userId) {
        return CreateNoteCommand.builder()
            .userId(userId)
            .directoryId(request.directoryId())
            .title(request.title())
            .content(request.content())
            .build();
    }

    public UpdateNoteCommand toCommand(UpdateNoteRequest request) {
        return UpdateNoteCommand.builder()
            .title(request.title())
            .content(request.content())
            .build();
    }
}
