package org.art.vertex.infrastructure.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.exception.NoteNotFoundException;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.art.vertex.infrastructure.note.jpa.NoteJpaRepository;
import org.art.vertex.infrastructure.note.mapper.NoteEntityMapper;
import org.art.vertex.infrastructure.note.updater.NoteUpdater;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DefaultNoteRepository implements NoteRepository {

    private final NoteJpaRepository noteJpaRepository;

    private final NoteEntityMapper noteMapper;

    private final NoteUpdater noteUpdater;

    @Override
    @Transactional
    public Note save(Note newNote) {
        NoteEntity entity = noteMapper.toEntity(newNote);
        NoteEntity savedEntity = noteJpaRepository.save(entity);
        return noteMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public List<Note> saveAll(List<Note> notes) {
        List<NoteEntity> entities = notes.stream()
            .map(noteMapper::toEntity)
            .toList();

        List<NoteEntity> savedEntities = noteJpaRepository.saveAll(entities);

        return savedEntities.stream()
            .map(noteMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public Note update(Note updatedNote) {
        NoteEntity existingNote = noteJpaRepository.findById(updatedNote.getId())
            .orElseThrow(() ->
                new NoteNotFoundException("Note cannot be found. Note id: %s".formatted(updatedNote.getId().toString()))
            );

        noteUpdater.updateNote(existingNote, updatedNote);

        NoteEntity updatedEntity = noteJpaRepository.save(existingNote);

        return noteMapper.toDomain(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Note getByNoteIdAndUserId(UUID noteId, UUID userId) {
        return findByNoteIdAndUserId(noteId, userId)
            .orElseThrow(() ->
                new NoteNotFoundException("Note cannot be found. Note id: %s, user id: %s"
                    .formatted(noteId, userId))
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Note> findByNoteIdAndUserId(UUID noteId, UUID userId) {
        return noteJpaRepository.findByNoteIdAndUserId(noteId, userId)
            .map(noteMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAll(UUID userId) {
        return noteJpaRepository.findAllByUserIdOrderByUpdatedAtDesc(userId)
            .stream()
            .map(noteMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAllByDirectory(Directory directory) {
        return noteJpaRepository.findAllByDirId(directory.getId())
            .stream()
            .map(noteMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAllByDirectoryTree(Directory directory) {
        // TODO: Implement recursive directory tree traversal
        return findAllByDirectory(directory);
    }

    @Transactional
    public void deleteByNoteIdAndUserId(UUID noteId, UUID userId) {
        NoteEntity entity = noteJpaRepository.findByNoteIdAndUserId(noteId, userId)
            .orElseThrow(() ->
                new NoteNotFoundException("Note cannot be found. Note id: %s".formatted(noteId))
            );

        noteJpaRepository.delete(entity);
    }
}
