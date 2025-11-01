package org.art.vertex.web.directory.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateDirectoryRequest(

    @NotBlank(message = "Directory name is required")
    @Size(max = 255, message = "Directory name must not exceed 255 characters")
    String name,

    UUID parentId
) {
}
