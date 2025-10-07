package org.art.vertex.web.note.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateNoteRequest(

    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    String content
) {
}
