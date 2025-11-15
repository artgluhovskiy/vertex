package org.art.vertex.web.note.search.mapper;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.note.search.model.SearchHit;
import org.art.vertex.domain.note.search.model.SearchResult;
import org.art.vertex.web.note.dto.NoteDto;
import org.art.vertex.web.note.mapper.NoteDtoMapper;
import org.art.vertex.web.note.search.dto.SearchHitDto;
import org.art.vertex.web.note.search.dto.SearchResultDto;

import java.util.List;

@RequiredArgsConstructor
public class SearchDtoMapper {

    private final NoteDtoMapper noteDtoMapper;

    public SearchResultDto toDto(SearchResult result) {
        List<SearchHitDto> hitDtos = result.getHits().stream()
            .map(this::toHitDto)
            .toList();

        return SearchResultDto.builder()
            .hits(hitDtos)
            .totalHits(result.getTotalHits())
            .build();
    }

    private SearchHitDto toHitDto(SearchHit hit) {
        NoteDto noteDto = noteDtoMapper.toDto(hit.getNote());

        return SearchHitDto.builder()
            .note(noteDto)
            .score(hit.getScore())
            .build();
    }
}
