package org.art.vertex.web.directory.dto;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.web.note.dto.NoteDto;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class DirectoryTreeDto {
    UUID id;
    String name;
    UUID parentId;
    List<DirectoryTreeDto> children;
    List<NoteDto> notes;
}
