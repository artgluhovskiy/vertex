package org.art.vertex.infrastructure.note.link.mapper;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.model.NoteLink;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.note.link.entity.NoteLinkEntity;

@RequiredArgsConstructor
public class NoteLinkEntityMapper {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteLinkEntity toEntity(NoteLink noteLink) {
        return NoteLinkEntity.builder()
            .id(noteLink.getId())
            .userId(noteLink.getUser().getId())
            .sourceNoteId(noteLink.getSourceNote().getId())
            .targetNoteId(noteLink.getTargetNote().getId())
            .type(noteLink.getType())
            .createdAt(noteLink.getCreatedTs())
            .updatedAt(noteLink.getUpdatedTs())
            .build();
    }

    public NoteLink toDomain(NoteLinkEntity entity) {
        return NoteLink.builder()
            .id(entity.getId())
            .user(userRepository.getById(entity.getUserId()))
            .sourceNote(noteRepository.getByNoteIdAndUserId(entity.getSourceNoteId(), entity.getUserId()))
            .targetNote(noteRepository.getByNoteIdAndUserId(entity.getTargetNoteId(), entity.getUserId()))
            .type(entity.getType())
            .createdTs(entity.getCreatedAt())
            .updatedTs(entity.getUpdatedAt())
            .build();
    }
}
