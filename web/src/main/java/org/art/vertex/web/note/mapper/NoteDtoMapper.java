package org.art.vertex.web.note.mapper;

import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.web.note.dto.NoteDto;
import org.art.vertex.web.tag.dto.TagDto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class NoteDtoMapper {

    public NoteDto toDto(Note note) {
        return NoteDto.builder()
            .id(note.getId())
            .userId(note.getUserId())
            .dirId(note.getDir() != null ? note.getDir().getId() : null)
            .title(note.getTitle())
            .content(note.getContent())
            .summary(note.getSummary())
            .tags(mapTagsToDto(note.getTags()))
            .metadata(note.getMetadata())
            .createdAt(note.getCreatedTs())
            .updatedAt(note.getUpdatedTs())
            .build();
    }

    public Note toDomain(
        NoteDto dto,
        UUID userId,
        Directory directory,
        Set<Tag> tags
    ) {
        return Note.builder()
            .id(dto.id())
            .userId(userId)
            .dir(directory)
            .title(dto.title())
            .content(dto.content())
            .summary(dto.summary())
            .tags(tags != null ? tags : Set.of())
            .metadata(dto.metadata() != null ? dto.metadata() : java.util.Map.of())
            .createdTs(dto.createdAt())
            .updatedTs(dto.updatedAt())
            .build();
    }

    private Set<TagDto> mapTagsToDto(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Set.of();
        }
        return tags.stream()
            .map(tag -> TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build())
            .collect(Collectors.toSet());
    }
}
