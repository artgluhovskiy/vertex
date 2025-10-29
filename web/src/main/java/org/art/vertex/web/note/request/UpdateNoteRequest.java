package org.art.vertex.web.note.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
public record UpdateNoteRequest(

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,

    @NotBlank(message = "Content is required")
    String content,

    @NotNull(message = "Directory id is required")
    UUID dirId,

    Set<String> tags
) {
}
