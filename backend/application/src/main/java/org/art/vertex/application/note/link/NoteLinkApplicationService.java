package org.art.vertex.application.note.link;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.note.link.command.CreateNoteLinkCommand;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.domain.note.NoteLinkRepository;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.model.NoteLink;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.user.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class NoteLinkApplicationService {

    private final NoteLinkRepository noteLinkRepository;
    private final NoteApplicationService noteApplicationService;
    private final UserApplicationService userApplicationService;
    private final UuidGenerator uuidGenerator;
    private final Clock clock;

    /**
     * Create a link between two notes.
     *
     * @param userId user ID
     * @param command create link command
     * @return created note link
     */
    @Transactional
    public NoteLink createLink(UUID userId, CreateNoteLinkCommand command) {
        log.debug("Creating note link. User id: {}, source: {}, target: {}",
            userId, command.getSourceNoteId(), command.getTargetNoteId());

        User user = userApplicationService.getById(userId);
        Note sourceNote = noteApplicationService.getNote(command.getSourceNoteId(), userId);
        Note targetNote = noteApplicationService.getNote(command.getTargetNoteId(), userId);

        // Check if link already exists
        if (noteLinkRepository.existsBetweenNotes(sourceNote, targetNote)) {
            log.warn("Link already exists between notes {} and {}", command.getSourceNoteId(), command.getTargetNoteId());
            throw new IllegalStateException("Link already exists between these notes");
        }

        LocalDateTime now = clock.now();

        NoteLink link = NoteLink.create(
            uuidGenerator.generate(),
            user,
            sourceNote,
            targetNote,
            command.getType(),
            now
        );

        NoteLink savedLink = noteLinkRepository.save(link);

        log.info("Note link created successfully. Link id: {}, source: {}, target: {}",
            savedLink.getId(), command.getSourceNoteId(), command.getTargetNoteId());

        return savedLink;
    }

    /**
     * Get all outgoing links from a note.
     *
     * @param noteId note ID
     * @param userId user ID
     * @return list of outgoing links
     */
    @Transactional(readOnly = true)
    public List<NoteLink> getOutgoingLinks(UUID noteId, UUID userId) {
        log.debug("Fetching outgoing links. Note id: {}, user id: {}", noteId, userId);

        Note note = noteApplicationService.getNote(noteId, userId);
        return noteLinkRepository.findAllBySourceNote(note);
    }

    /**
     * Get all incoming links to a note (backlinks).
     *
     * @param noteId note ID
     * @param userId user ID
     * @return list of incoming links (backlinks)
     */
    @Transactional(readOnly = true)
    public List<NoteLink> getIncomingLinks(UUID noteId, UUID userId) {
        log.debug("Fetching incoming links (backlinks). Note id: {}, user id: {}", noteId, userId);

        Note note = noteApplicationService.getNote(noteId, userId);
        return noteLinkRepository.findAllByTargetNote(note);
    }

    /**
     * Get all links for a note (both incoming and outgoing).
     *
     * @param noteId note ID
     * @param userId user ID
     * @return list of all links
     */
    @Transactional(readOnly = true)
    public List<NoteLink> getAllLinks(UUID noteId, UUID userId) {
        log.debug("Fetching all links. Note id: {}, user id: {}", noteId, userId);

        Note note = noteApplicationService.getNote(noteId, userId);
        return noteLinkRepository.findAllByNote(note);
    }

    /**
     * Delete a link between two notes.
     *
     * @param linkId link ID
     * @param userId user ID (for authorization)
     */
    @Transactional
    public void deleteLink(UUID linkId, UUID userId) {
        log.debug("Deleting note link. Link id: {}, user id: {}", linkId, userId);

        NoteLink link = noteLinkRepository.getById(linkId);

        // Verify user owns the link
        if (!link.getUser().getId().equals(userId)) {
            log.warn("User {} attempted to delete link {} belonging to user {}",
                userId, linkId, link.getUser().getId());
            throw new IllegalStateException("User does not have permission to delete this link");
        }

        noteLinkRepository.deleteById(linkId);

        log.info("Note link deleted successfully. Link id: {}", linkId);
    }

    /**
     * Delete all links for a note (used when deleting a note).
     *
     * @param noteId note ID
     * @param userId user ID
     */
    @Transactional
    public void deleteAllLinksForNote(UUID noteId, UUID userId) {
        log.debug("Deleting all links for note. Note id: {}, user id: {}", noteId, userId);

        Note note = noteApplicationService.getNote(noteId, userId);
        noteLinkRepository.deleteByNote(note);

        log.info("All links deleted for note. Note id: {}", noteId);
    }
}
