package org.art.vertex.infrastructure.note.mapper;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.note.entity.NoteEntity;

@RequiredArgsConstructor
public class NoteEntityMapper {

    private final UserRepository userRepository;

    public NoteEntity toEntity(Note note) {
        return NoteEntity.builder()
            .id(note.getId())
            .userId(note.getUser().getId())
            .directoryId(note.getDirectory() != null ? note.getDirectory().getId() : null)
            .title(note.getTitle())
            .content(note.getContent())
            .summary(note.getSummary())
            .metadata(note.getMetadata())
            .createdAt(note.getCreatedTs())
            .updatedAt(note.getUpdatedTs())
            .version(note.getVersion())
            .build();
    }

    public Note toDomain(NoteEntity entity) {
        return Note.builder()
            .id(entity.getId())
            .user(userRepository.getById(entity.getUserId()))
            .directory(null) // Will be loaded separately when needed
            .title(entity.getTitle())
            .content(entity.getContent())
            .summary(entity.getSummary())
            .metadata(entity.getMetadata())
            .createdTs(entity.getCreatedAt())
            .updatedTs(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}
