package org.art.vertex.web.note.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UpdateNoteRequest(

    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    String content,

    UUID dirId,

    List<String> tags
) {
}
