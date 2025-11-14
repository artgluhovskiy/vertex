package org.art.vertex.domain.note.search.model;

import lombok.Builder;
import lombok.Value;

/**
 * Search configuration for quality thresholds and weights.
 */
@Value
@Builder
public class SearchConfiguration {

    @Builder.Default
    double ftsMinRankThreshold = 0.01;

    @Builder.Default
    double vectorMinSimilarityThreshold = 0.5;

    @Builder.Default
    double hybridVectorWeight = 0.6;

    @Builder.Default
    double hybridFtsWeight = 0.4;

    @Builder.Default
    double hybridOverlapBonus = 0.2;

    @Builder.Default
    double hybridFinalMinThreshold = 0.15;

    @Builder.Default
    int defaultMaxResults = 20;
}
