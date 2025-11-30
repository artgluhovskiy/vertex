package org.art.vertex.infrastructure.note.link;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.NoteLinkRepository;
import org.art.vertex.domain.note.exception.NoteLinkNotFoundException;
import org.art.vertex.domain.note.model.LinkType;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.model.NoteLink;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.infrastructure.note.link.entity.NoteLinkEntity;
import org.art.vertex.infrastructure.note.link.jpa.NoteLinkJpaRepository;
import org.art.vertex.infrastructure.note.link.mapper.NoteLinkEntityMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class DefaultNoteLinkRepository implements NoteLinkRepository {

    private final NoteLinkJpaRepository noteLinkJpaRepository;
    private final NoteLinkEntityMapper noteLinkMapper;

    @Override
    @Transactional
    public NoteLink save(NoteLink noteLink) {
        NoteLinkEntity entity = noteLinkMapper.toEntity(noteLink);
        NoteLinkEntity savedEntity = noteLinkJpaRepository.save(entity);
        return noteLinkMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public NoteLink getById(UUID id) {
        return findById(id)
            .orElseThrow(() ->
                new NoteLinkNotFoundException("NoteLink cannot be found. Link id: %s".formatted(id))
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NoteLink> findById(UUID id) {
        return noteLinkJpaRepository.findById(id)
            .map(noteLinkMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteLink> findAllBySourceNote(Note sourceNote) {
        return noteLinkJpaRepository.findAllBySourceNoteId(sourceNote.getId())
            .stream()
            .map(noteLinkMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteLink> findAllByTargetNote(Note targetNote) {
        return noteLinkJpaRepository.findAllByTargetNoteId(targetNote.getId())
            .stream()
            .map(noteLinkMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteLink> findAllByNote(Note note) {
        List<NoteLinkEntity> outgoing = noteLinkJpaRepository.findAllBySourceNoteId(note.getId());
        List<NoteLinkEntity> incoming = noteLinkJpaRepository.findAllByTargetNoteId(note.getId());

        return Stream.concat(outgoing.stream(), incoming.stream())
            .map(noteLinkMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteLink> findAllByUser(User user) {
        return noteLinkJpaRepository.findAllByUserId(user.getId())
            .stream()
            .map(noteLinkMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBetweenNotes(Note sourceNote, Note targetNote) {
        return noteLinkJpaRepository.existsBetweenNotes(
            sourceNote.getId(),
            targetNote.getId(),
            sourceNote.getUserId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBetweenNotes(Note sourceNote, Note targetNote, LinkType linkType) {
        return noteLinkJpaRepository.existsBetweenNotesWithType(
            sourceNote.getId(),
            targetNote.getId(),
            linkType,
            sourceNote.getUserId()
        );
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        noteLinkJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByNote(Note note) {
        List<NoteLinkEntity> links = Stream.concat(
            noteLinkJpaRepository.findAllBySourceNoteId(note.getId()).stream(),
            noteLinkJpaRepository.findAllByTargetNoteId(note.getId()).stream()
        ).toList();

        noteLinkJpaRepository.deleteAll(links);
    }
}
