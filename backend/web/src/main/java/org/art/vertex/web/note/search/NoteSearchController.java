package org.art.vertex.web.note.search;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.note.search.NoteSearchApplicationService;
import org.art.vertex.application.note.search.command.SearchCommand;
import org.art.vertex.domain.note.search.model.SearchResult;
import org.art.vertex.web.note.search.dto.SearchResultDto;
import org.art.vertex.web.note.search.mapper.SearchCommandMapper;
import org.art.vertex.web.note.search.mapper.SearchDtoMapper;
import org.art.vertex.web.note.search.request.SearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/notes/search")
@RequiredArgsConstructor
public class NoteSearchController {

    private final NoteSearchApplicationService searchService;

    private final SearchCommandMapper searchCommandMapper;

    private final SearchDtoMapper searchDtoMapper;

    @PostMapping
    public ResponseEntity<SearchResultDto> search(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody SearchRequest request
    ) {
        log.trace("Processing search request. User id: {}, query: '{}'", userId, request.query());

        SearchCommand command = searchCommandMapper.toCommand(request, UUID.fromString(userId));

        SearchResult result = searchService.search(command);

        SearchResultDto resultDto = searchDtoMapper.toDto(result);

        log.debug("Search completed. User id: {}, query: '{}', hits: {}",
            userId, request.query(), result.getTotalHits());

        return ResponseEntity.ok(resultDto);
    }
}
