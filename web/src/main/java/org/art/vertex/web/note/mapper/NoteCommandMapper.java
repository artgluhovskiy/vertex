package org.art.vertex.web.note.mapper;

import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.web.note.request.CreateNoteRequest;
import org.art.vertex.web.note.request.UpdateNoteRequest;

import java.util.Set;

public class NoteCommandMapper {

    public CreateNoteCommand toCommand(CreateNoteRequest request) {
        return CreateNoteCommand.builder()
            .dirId(request.dirId())
            .title(request.title())
            .content(request.content())
            .tags(request.tags() != null ? request.tags() : Set.of())
            .build();
    }

    public UpdateNoteCommand toCommand(UpdateNoteRequest request) {
        return UpdateNoteCommand.builder()
            .title(request.title())
            .content(request.content())
            .dirId(request.dirId())
            .tags(request.tags() != null ? request.tags() : Set.of())
            .build();
    }
}
