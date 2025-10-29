package org.art.vertex.web.directory.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateDirectoryRequest(

    @Size(max = 255, message = "Directory name must not exceed 255 characters")
    String name,

    UUID parentId
) {
}
