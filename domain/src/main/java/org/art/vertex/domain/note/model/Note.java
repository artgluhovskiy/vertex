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
        List<Tag> tags,
        LocalDateTime ts
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("Title too long (max 255 characters)");
        }

        return Note.builder()
            .id(id)
            .user(user)
            .title(title)
            .content(content)
            .directory(directory)
            .tags(tags)
            .createdTs(ts)
            .updatedTs(ts)
            .version(null)
            .build();
    }

    public Note updateTitle(String newTitle, LocalDateTime updatedTs) {
        validateTitle(newTitle);
        return this.toBuilder()
            .title(newTitle)
            .updatedTs(updatedTs)
            .version(version + 1)
            .build();
    }

    public Note updateContent(String newContent, LocalDateTime updatedTs) {
        return this.toBuilder()
            .content(newContent)
            .updatedTs(updatedTs)
            .version(version + 1)
            .build();
    }

    public Note update(String newTitle, String newContent, LocalDateTime updatedTs) {
        String effectiveTitle = newTitle != null ? newTitle : this.title;
        String effectiveContent = newContent != null ? newContent : this.content;

        if (newTitle != null) {
            validateTitle(newTitle);
        }

        return this.toBuilder()
            .title(effectiveTitle)
            .content(effectiveContent)
            .updatedTs(updatedTs)
            .version(version + 1)
            .build();
    }

    public Note updateDirectory(Directory newDirectory, LocalDateTime updatedTs) {
        return this.toBuilder()
            .directory(newDirectory)
            .updatedTs(updatedTs)
            .version(version + 1)
            .build();
    }

    public Note addTags(List<Tag> tagsToAdd, LocalDateTime updatedTs) {
        List<Tag> updatedTags = new java.util.ArrayList<>(this.tags);

        for (Tag tag : tagsToAdd) {
            if (!containsTag(tag.getId())) {
                updatedTags.add(tag);
            }
        }

        return this.toBuilder()
            .tags(updatedTags)
            .updatedTs(updatedTs)
            .version(version + 1)
            .build();
    }

    public Note removeTags(List<UUID> tagIdsToRemove, LocalDateTime updatedTs) {
        List<Tag> updatedTags = this.tags.stream()
            .filter(tag -> !tagIdsToRemove.contains(tag.getId()))
            .toList();

        return this.toBuilder()
            .tags(updatedTags)
            .updatedTs(updatedTs)
            .version(version + 1)
            .build();
    }

    public Note update(
        String newTitle,
        String newContent,
        Directory newDirectory,
        List<Tag> newTags,
        LocalDateTime updatedTs
    ) {
        String effectiveTitle = newTitle != null ? newTitle : this.title;
        String effectiveContent = newContent != null ? newContent : this.content;
        Directory effectiveDirectory = newDirectory != null ? newDirectory : this.directory;
        List<Tag> effectiveTags = newTags != null ? newTags : this.tags;

        if (newTitle != null) {
            validateTitle(newTitle);
        }

        return this.toBuilder()
            .title(effectiveTitle)
            .content(effectiveContent)
            .directory(effectiveDirectory)
            .tags(effectiveTags)
            .updatedTs(updatedTs)
            .version(version + 1)
            .build();
    }

    private boolean containsTag(UUID tagId) {
        return this.tags.stream().anyMatch(tag -> tag.getId().equals(tagId));
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("Title too long (max 255 characters)");
        }
    }
}