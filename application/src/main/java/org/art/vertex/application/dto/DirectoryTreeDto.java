package org.art.vertex.application.dto;

import lombok.Builder;
import lombok.Value;

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