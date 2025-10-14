package org.art.vertex.web.note.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.art.vertex.web.tag.dto.TagDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record NoteDto(
    UUID id,
    UUID userId,
    UUID dirId,
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,
    String content,
    String summary,
    List<TagDto> tags,
    Map<String, Object> metadata,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Integer version
) {
}
