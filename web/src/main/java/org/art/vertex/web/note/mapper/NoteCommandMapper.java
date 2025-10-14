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
            .dirId(request.dirId())
            .title(request.title())
            .content(request.content())
            .build();
    }

    public UpdateNoteCommand toCommand(UpdateNoteRequest request) {
        return UpdateNoteCommand.builder()
            .title(request.title())
            .content(request.content())
            .dirId(request.directoryId())
            .tagIds(request.tagIds())
            .build();
    }
}
