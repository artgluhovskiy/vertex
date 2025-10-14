package org.art.vertex.web.note.mapper;

import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.web.note.dto.NoteDto;
import org.art.vertex.web.tag.dto.TagDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NoteDtoMapper {

    public NoteDto toDto(Note note) {
        return NoteDto.builder()
            .id(note.getId())
            .userId(note.getUser().getId())
            .dirId(note.getDirectory() != null ? note.getDirectory().getId() : null)
            .title(note.getTitle())
            .content(note.getContent())
            .summary(note.getSummary())
            .tags(mapTagsToDto(note.getTags()))
            .metadata(note.getMetadata())
            .createdAt(note.getCreatedTs())
            .updatedAt(note.getUpdatedTs())
            .version(note.getVersion())
            .build();
    }

    public Note toDomain(NoteDto dto, User user, Directory directory, List<Tag> tags) {
        return Note.builder()
            .id(dto.id())
            .user(user)
            .directory(directory)
            .title(dto.title())
            .content(dto.content())
            .summary(dto.summary())
            .tags(tags != null ? tags : List.of())
            .metadata(dto.metadata() != null ? dto.metadata() : java.util.Map.of())
            .createdTs(dto.createdAt())
            .updatedTs(dto.updatedAt())
            .version(dto.version())
            .build();
    }

    private List<TagDto> mapTagsToDto(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
            .map(tag -> TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .build())
            .collect(Collectors.toList());
    }
}
