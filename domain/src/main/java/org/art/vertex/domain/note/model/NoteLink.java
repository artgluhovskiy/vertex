package org.art.vertex.domain.note.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class NoteLink {

    UUID id;

    User user;

    Note sourceNote;

    Note targetNote;

    LinkType type;

    LocalDateTime createdTs;

    LocalDateTime updatedTs;

    public static NoteLink create(
        UUID id,
        User user,
        Note sourceNote,
        Note targetNote,
        LinkType type,
        LocalDateTime ts
    ) {
        return NoteLink.builder()
            .id(id)
            .user(user)
            .sourceNote(sourceNote)
            .targetNote(targetNote)
            .type(type)
            .createdTs(ts)
            .updatedTs(ts)
            .build();
    }
}