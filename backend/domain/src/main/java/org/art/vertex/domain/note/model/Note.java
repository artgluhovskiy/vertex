package org.art.vertex.domain.note.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.tag.model.Tag;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Note {

    UUID id;

    UUID userId;

    Directory dir;

    String title;

    String content;

    String summary;

    @Builder.Default
    Set<Tag> tags = Set.of();

    @Builder.Default
    Map<String, Object> metadata = Map.of();

    LocalDateTime updatedTs;

    LocalDateTime createdTs;

    // Version for the Optimistic Lock check
    Integer version;

    public static Note create(
        UUID id,
        UUID userId,
        String title,
        String content,
        Directory directory,
        Set<Tag> tags,
        LocalDateTime ts
    ) {
        return Note.builder()
            .id(id)
            .userId(userId)
            .title(title)
            .content(content)
            .dir(directory)
            .tags(tags)
            .createdTs(ts)
            .updatedTs(ts)
            .version(null)
            .build();
    }

    public Note update(
        String newTitle,
        String newContent,
        Directory newDir,
        Set<Tag> newTags,
        LocalDateTime ts
    ) {
        NoteBuilder noteBuilder = toBuilder()
            .updatedTs(ts);

        if (newTitle != null) {
            noteBuilder.title(newTitle);
        }

        if (newContent != null) {
            noteBuilder.content(newContent);
        }

        if (newDir != null) {
            noteBuilder.dir(newDir);
        }

        if (newTags != null) {
            noteBuilder.tags(newTags);
        }

        return noteBuilder.build();
    }
}