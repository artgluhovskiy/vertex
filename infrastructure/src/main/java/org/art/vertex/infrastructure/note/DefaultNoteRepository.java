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

    @Override
    @Transactional
    public Note save(Note note) {
        NoteEntity entity = noteMapper.toEntity(note);
        NoteEntity savedEntity = noteJpaRepository.save(entity);
        return noteMapper.toDomain(savedEntity);
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
    public Optional<Note> findById(UUID id) {
        return noteJpaRepository.findById(id)
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
        // TODO: Implement tag-based search
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAllByTagNames(User user, List<String> tagNames) {
        // TODO: Implement tag name-based search
        return List.of();
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
}
