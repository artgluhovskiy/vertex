package org.art.vertex.web.note.mapper;

import org.art.vertex.domain.note.model.Note;
import org.art.vertex.web.note.dto.NoteDto;

import java.util.List;

public class NoteDtoMapper {

    public NoteDto toDto(Note note) {
        return NoteDto.builder()
            .id(note.getId())
            .userId(note.getUser().getId())
            .directoryId(note.getDirectory() != null ? note.getDirectory().getId() : null)
            .title(note.getTitle())
            .content(note.getContent())
            .summary(note.getSummary())
            .tags(List.of()) // TODO: Map tags when tag functionality is implemented
            .metadata(note.getMetadata())
            .createdAt(note.getCreatedTs())
            .updatedAt(note.getUpdatedTs())
            .version(note.getVersion())
            .build();
    }
}
