package org.art.vertex.domain.note.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Note {

    UUID id;

    User user;

    Directory directory;

    String title;

    String content;

    String summary;

    @Builder.Default
    List<Tag> tags = List.of();

    @Builder.Default
    Map<String, Object> metadata = Map.of();

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
            .version(null)
            .build();
    }
}