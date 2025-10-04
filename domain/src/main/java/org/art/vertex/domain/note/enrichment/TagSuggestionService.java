package org.art.vertex.domain.note.enrichment;

import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.tag.model.Tag;

import java.util.List;

public interface TagSuggestionService {

    List<Tag> suggestTags(Note note);
}