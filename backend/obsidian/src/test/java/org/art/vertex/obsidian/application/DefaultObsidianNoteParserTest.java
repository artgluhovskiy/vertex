package org.art.vertex.obsidian.application;

import org.art.vertex.obsidian.application.parser.ObsidianMetadataExtractor;
import org.art.vertex.obsidian.domain.model.ObsidianNote;
import org.art.vertex.obsidian.domain.model.WikilinkReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultObsidianNoteParser Unit Tests")
class DefaultObsidianNoteParserTest {

    @Mock
    private ObsidianMetadataExtractor metadataExtractor;

    private DefaultObsidianNoteParser parser;

    @BeforeEach
    void setUp() {
        parser = new DefaultObsidianNoteParser(metadataExtractor);
    }

    @Test
    @DisplayName("Should parse note with frontmatter")
    void shouldParseNoteWithFrontmatter() {
        // Given
        var fileContent = """
            ---
            title: Test Note
            tags: [test, example]
            ---
            # Test Content
            """;
        var frontmatter = Map.<String, Object>of(
            "title", "Test Note",
            "tags", List.of("test", "example")
        );
        var contentWithoutFrontmatter = "# Test Content\n";
        var created = LocalDateTime.of(2024, 1, 15, 10, 0);
        var modified = LocalDateTime.of(2024, 1, 16, 15, 30);

        when(metadataExtractor.extractFrontmatter(fileContent)).thenReturn(frontmatter);
        when(metadataExtractor.removeFrontmatter(fileContent)).thenReturn(contentWithoutFrontmatter);

        // When
        var result = parser.parse(fileContent, "test.md", "test.md", created, modified);

        // Then
        assertThat(result.getTitle()).isEqualTo("Test Note");
        assertThat(result.getTags()).containsExactlyInAnyOrder("test", "example");
        assertThat(result.getContent()).isEqualTo(contentWithoutFrontmatter);
        assertThat(result.getFileName()).isEqualTo("test.md");
        assertThat(result.getCreated()).isEqualTo(created);
        assertThat(result.getModified()).isEqualTo(modified);
    }

    @Test
    @DisplayName("Should parse note without frontmatter and derive title from filename")
    void shouldParseNoteWithoutFrontmatter() {
        // Given
        var content = "# Simple Note\n\nContent here";

        when(metadataExtractor.extractFrontmatter(any())).thenReturn(Map.of());
        when(metadataExtractor.removeFrontmatter(any())).thenReturn(content);

        // When
        var result = parser.parse(content, "My Note.md", "path/My Note.md",
            LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(result.getTitle()).isEqualTo("My Note");
        assertThat(result.getTags()).isEmpty();
    }

    @Test
    @DisplayName("Should extract wikilinks from content")
    void shouldExtractWikilinks() {
        // Given
        var content = """
            Link to [[Other Note]]
            Link with alias [[Target|Display Text]]
            Link with heading [[Note#Heading]]
            Embedded image ![[image.png]]
            """;

        when(metadataExtractor.extractFrontmatter(any())).thenReturn(Map.of());
        when(metadataExtractor.removeFrontmatter(any())).thenReturn(content);

        // When
        var result = parser.parse(content, "test.md", "test.md",
            LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(result.getWikilinks()).hasSize(4);

        assertThat(result.getWikilinks())
            .extracting(WikilinkReference::getTargetNoteName)
            .containsExactly("Other Note", "Target", "Note", "image.png");

        assertThat(result.getWikilinks().get(1).getDisplayText()).isEqualTo("Display Text");
        assertThat(result.getWikilinks().get(2).getAnchor()).isEqualTo("Heading");
        assertThat(result.getWikilinks().get(3).isEmbedded()).isTrue();
    }

    @Test
    @DisplayName("Should extract inline tags from content")
    void shouldExtractInlineTags() {
        // Given
        var content = "Some content with #tag1 and #tag2/nested tags";

        when(metadataExtractor.extractFrontmatter(any())).thenReturn(Map.of());
        when(metadataExtractor.removeFrontmatter(any())).thenReturn(content);

        // When
        var result = parser.parse(content, "test.md", "test.md",
            LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(result.getTags()).containsExactlyInAnyOrder("tag1", "tag2/nested");
    }

    @Test
    @DisplayName("Should combine frontmatter and inline tags")
    void shouldCombineFrontmatterAndInlineTags() {
        // Given
        var content = "Content with #inline-tag";
        var frontmatter = Map.<String, Object>of("tags", List.of("frontmatter-tag"));

        when(metadataExtractor.extractFrontmatter(any())).thenReturn(frontmatter);
        when(metadataExtractor.removeFrontmatter(any())).thenReturn(content);

        // When
        var result = parser.parse(content, "test.md", "test.md",
            LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(result.getTags()).containsExactlyInAnyOrder("frontmatter-tag", "inline-tag");
    }

    @Test
    @DisplayName("Should include filename as default alias")
    void shouldIncludeFilenameAsAlias() {
        // Given
        when(metadataExtractor.extractFrontmatter(any())).thenReturn(Map.of());
        when(metadataExtractor.removeFrontmatter(any())).thenReturn("Content");

        // When
        var result = parser.parse("Content", "My Note.md", "My Note.md",
            LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(result.getAliases()).contains("My Note");
    }

    @Test
    @DisplayName("Should extract directory path from file path")
    void shouldExtractDirectoryPath() {
        // Given
        when(metadataExtractor.extractFrontmatter(any())).thenReturn(Map.of());
        when(metadataExtractor.removeFrontmatter(any())).thenReturn("Content");

        // When
        var result = parser.parse("Content", "note.md", "folder/subfolder/note.md",
            LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(result.getDirectoryPath()).isEqualTo("folder/subfolder");
    }

    @Test
    @DisplayName("Should handle root directory (no path)")
    void shouldHandleRootDirectory() {
        // Given
        when(metadataExtractor.extractFrontmatter(any())).thenReturn(Map.of());
        when(metadataExtractor.removeFrontmatter(any())).thenReturn("Content");

        // When
        var result = parser.parse("Content", "note.md", "note.md",
            LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(result.getDirectoryPath()).isEmpty();
    }
}
