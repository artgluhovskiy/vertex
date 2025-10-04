package org.art.vertex.application.note.dto;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.application.tag.dto.TagDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class NoteDto {
    UUID id;
    UUID userId;
    UUID directoryId;
    String title;
    String content;
    String summary;
    List<TagDto> tags;
    Map<String, Object> metadata;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer version;
}