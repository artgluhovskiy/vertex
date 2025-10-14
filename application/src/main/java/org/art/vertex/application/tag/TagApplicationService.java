package org.art.vertex.application.tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.tag.command.UpsertTagCommand;
import org.art.vertex.domain.tag.model.Tag;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TagApplicationService {

    /**
     * Creates tags if not exist or update if exists.
     *
     * @param userId   user id
     * @param commands upsert tag commands
     * @return created or updated tags
     */
    @Transactional
    public Set<Tag> upsertTags(UUID userId, Set<UpsertTagCommand> commands) {
        return null;
    }
}
