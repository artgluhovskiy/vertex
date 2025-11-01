package org.art.vertex.application.tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.tag.command.UpsertTagCommand;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.tag.TagRepository;
import org.art.vertex.domain.tag.model.Tag;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TagApplicationService {

    private final TagRepository tagRepository;

    private final UuidGenerator uuidGenerator;

    private final Clock clock;

    /**
     * Creates tags if not exist or update if exists.
     *
     * @param userId   user id
     * @param commands upsert tag commands
     * @return created or updated tags
     */
    @Transactional
    public Set<Tag> upsertTags(UUID userId, Set<UpsertTagCommand> commands) {
        log.debug("Upserting tags. User id: {}, tags count: {}", userId, commands.size());

        if (commands.isEmpty()) {
            log.debug("No tags to upsert. Returning empty set.");
            return Set.of();
        }

        LocalDateTime now = clock.now();

        Set<Tag> tags = new HashSet<>();

        for (UpsertTagCommand command : commands) {
            String tagName = command.name().trim();

            // Try to find existing tag by name and user
            Tag tag = tagRepository.findByNameAndUserId(tagName, userId)
                .orElseGet(() -> {
                    log.debug("Creating new tag. Name: {}, user id: {}", tagName, userId);
                    return Tag.create(
                        uuidGenerator.generate(),
                        userId,
                        tagName,
                        now
                    );
                });

            Tag savedTag = tagRepository.save(tag);

            tags.add(savedTag);
        }

        log.info("Upserted {} tags for user: {}", tags.size(), userId);

        return tags;
    }
}
