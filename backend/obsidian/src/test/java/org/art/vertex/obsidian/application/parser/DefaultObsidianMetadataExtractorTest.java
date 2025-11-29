package org.art.vertex.obsidian.application.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultObsidianMetadataExtractor Unit Tests")
class DefaultObsidianMetadataExtractorTest {

    private DefaultObsidianMetadataExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new DefaultObsidianMetadataExtractor(new Yaml());
    }

    @Test
    @DisplayName("Should extract valid YAML frontmatter")
    void shouldExtractValidFrontmatter() {
        // Given
        var fileContent = """
            ---
            title: Test Note
            tags: [tag1, tag2]
            status: draft
            ---
            # Content
            """;

        // When
        var result = extractor.extractFrontmatter(fileContent);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).containsEntry("title", "Test Note");
        assertThat(result).containsEntry("tags", List.of("tag1", "tag2"));
        assertThat(result).containsEntry("status", "draft");
    }

    @Test
    @DisplayName("Should return empty map when no frontmatter present")
    void shouldReturnEmptyMapWhenNoFrontmatter() {
        // Given
        var fileContent = "# Just content without frontmatter";

        // When
        var result = extractor.extractFrontmatter(fileContent);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle malformed YAML gracefully")
    void shouldHandleMalformedYaml() {
        // Given
        var fileContent = """
            ---
            title: Test
            invalid yaml here: [unclosed bracket
            ---
            # Content
            """;

        // When
        var result = extractor.extractFrontmatter(fileContent);

        // Then
        assertThat(result).isEmpty(); // Should return empty on parse error
    }

    @Test
    @DisplayName("Should handle null content")
    void shouldHandleNullContent() {
        // When
        var result = extractor.extractFrontmatter(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty content")
    void shouldHandleEmptyContent() {
        // When
        var result = extractor.extractFrontmatter("");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should remove frontmatter from content")
    void shouldRemoveFrontmatter() {
        // Given
        var fileContent = """
            ---
            title: Test
            ---
            # Actual Content
            More content here
            """;

        // When
        var result = extractor.removeFrontmatter(fileContent);

        // Then
        assertThat(result).doesNotContain("---");
        assertThat(result).doesNotContain("title: Test");
        assertThat(result).startsWith("# Actual Content");
        assertThat(result).contains("More content here");
    }

    @Test
    @DisplayName("Should return content unchanged when no frontmatter")
    void shouldReturnContentUnchangedWhenNoFrontmatter() {
        // Given
        var fileContent = "# Content without frontmatter";

        // When
        var result = extractor.removeFrontmatter(fileContent);

        // Then
        assertThat(result).isEqualTo(fileContent);
    }

    @Test
    @DisplayName("Should handle null content in removeFrontmatter")
    void shouldHandleNullContentInRemove() {
        // When
        var result = extractor.removeFrontmatter(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle nested YAML structures")
    void shouldHandleNestedYaml() {
        // Given
        var fileContent = """
            ---
            title: Test
            metadata:
              author: John Doe
              version: v1.0
            ---
            Content
            """;

        // When
        var result = extractor.extractFrontmatter(fileContent);

        // Then
        assertThat(result).containsKey("metadata");
        @SuppressWarnings("unchecked")
        var metadata = (Map<String, Object>) result.get("metadata");
        assertThat(metadata).containsEntry("author", "John Doe");
        assertThat(metadata).containsEntry("version", "v1.0");
    }
}
