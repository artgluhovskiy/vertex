package org.art.vertex.infrastructure.note;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.exception.NoteNotFoundException;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.art.vertex.infrastructure.note.jpa.NoteJpaRepository;
import org.art.vertex.infrastructure.note.mapper.NoteEntityMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DefaultNoteRepository implements NoteRepository {

    private final NoteJpaRepository noteJpaRepository;

    private final NoteEntityMapper noteMapper;

    private final org.art.vertex.infrastructure.note.updater.NoteUpdater noteUpdater;

    @Override
    @Transactional
    public Note save(Note note) {
        NoteEntity entity = noteMapper.toEntity(note);
        NoteEntity savedEntity = noteJpaRepository.save(entity);
        return noteMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public Note update(Note note) {
        // Load existing entity to maintain proper JPA state management
        NoteEntity existingEntity = noteJpaRepository.findById(note.getId())
            .orElseThrow(() ->
                new NoteNotFoundException("Note cannot be found. Note id: %s".formatted(note.getId().toString()))
            );

        // Delegate update logic to NoteUpdater
        noteUpdater.updateEntity(existingEntity, note);

        // JPA will automatically persist changes due to @Transactional
        NoteEntity updatedEntity = noteJpaRepository.save(existingEntity);
        return noteMapper.toDomain(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Note getById(UUID id) {
        return findById(id)
            .orElseThrow(() ->
                new NoteNotFoundException("Note cannot be found. Note id: %s".formatted(id.toString()))
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Note getByIdAndUser(UUID id, User user) {
        return findByIdAndUser(id, user)
            .orElseThrow(() ->
                new NoteNotFoundException("Note cannot be found. Note id: %s".formatted(id.toString()))
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Note> findById(UUID id) {
        return noteJpaRepository.findById(id)
            .map(noteMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Note> findByIdAndUser(UUID id, User user) {
        return noteJpaRepository.findById(id)
            .filter(entity -> entity.getUserId().equals(user.getId()))
            .map(noteMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAll(User user) {
        return noteJpaRepository.findAllByUserIdOrderByUpdatedAtDesc(user.getId())
            .stream()
            .map(noteMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAllByDirectory(Directory directory) {
        if (directory == null) {
            return List.of();
        }
        return noteJpaRepository.findAllByDirectoryId(directory.getId())
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

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAllByTags(User user, List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        List<UUID> tagIds = tags.stream()
            .map(Tag::getId)
            .toList();
        return noteJpaRepository.findAllByUserIdAndTagIds(user.getId(), tagIds)
            .stream()
            .map(noteMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAllByTagNames(User user, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return List.of();
        }
        return noteJpaRepository.findAllByUserIdAndTagNames(user.getId(), tagNames)
            .stream()
            .map(noteMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAllBySearchTerm(User user, String searchTerm) {
        // TODO: Implement hybrid search (semantic + full-text)
        return List.of();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        noteJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIdAndUser(UUID id, User user) {
        NoteEntity entity = noteJpaRepository.findById(id)
            .filter(e -> e.getUserId().equals(user.getId()))
            .orElseThrow(() ->
                new NoteNotFoundException("Note cannot be found. Note id: %s".formatted(id.toString()))
            );
        noteJpaRepository.delete(entity);
    }
}
