package org.art.vertex.domain.model.note;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.model.directory.Directory;
import org.art.vertex.domain.model.user.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class Note {

    UUID id;

    User user;

    Directory directory;

    String title;

    String content;

    String summary; // AI-generated summary

    Map<String, Object> metadata; // YAML frontmatter

    String fullTextVector; // For search indexing

    LocalDateTime updatedTs;

    LocalDateTime createdTs;

    // Version for the Optimistic Lock check
    Integer version;

    public static Note create(
        UUID id,
        User user,
        String title,
        String content,
        Directory directory,
        LocalDateTime ts
    ) {
        return Note.builder()
            .id(id)
            .user(user)
            .title(title)
            .content(content)
            .directory(directory)
            .createdTs(ts)
            .updatedTs(ts)
            .version(1)
            .build();
    }
}