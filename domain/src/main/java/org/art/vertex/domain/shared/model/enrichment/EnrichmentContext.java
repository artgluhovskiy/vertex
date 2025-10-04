package org.art.vertex.domain.shared.model.enrichment;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.user.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Value
@Builder
public class EnrichmentContext {

    User user;

    @Builder.Default
    Set<EnrichmentType> requestedTypes = Set.of();

    @Builder.Default
    Map<String, Object> parameters = Map.of();

    @Builder.Default
    List<EnrichmentResult> results = List.of();
}