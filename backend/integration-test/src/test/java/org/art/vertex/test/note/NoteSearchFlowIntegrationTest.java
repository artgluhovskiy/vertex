package org.art.vertex.test.note;

import lombok.SneakyThrows;
import org.art.vertex.domain.note.search.model.SearchType;
import org.art.vertex.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoteSearchFlowIntegrationTest extends BaseIntegrationTest {

    @Test
    @SneakyThrows
    void shouldIndexAndSearchNoteSemanticallySingleNote() {
        // GIVEN - User creates a note about machine learning
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "Tech Notes");
        var dirId = dir.id();

        noteSteps.createNote(
            token,
            "Introduction to Machine Learning",
            "Machine learning is a subset of artificial intelligence that enables systems to learn and improve from experience without being explicitly programmed. It focuses on developing computer programs that can access data and use it to learn for themselves.",
            dirId
        );

        // Wait for async indexing to complete
        Thread.sleep(3000);

        // WHEN - User searches for "artificial intelligence"
        var searchResult = searchSteps.search(token, "artificial intelligence", SearchType.SEMANTIC, 10);

        // THEN - The machine learning note should be found with high similarity
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.totalHits()).isEqualTo(1);
        assertThat(searchResult.hits()).hasSize(1);

        var hit = searchResult.hits().get(0);
        assertThat(hit.note().title()).isEqualTo("Introduction to Machine Learning");
        assertThat(hit.score()).isGreaterThan(0.5); // Minimum similarity threshold
    }

    @Test
    @SneakyThrows
    void shouldFindMostRelevantNoteAmongMultiple() {
        // GIVEN - User creates multiple notes with different topics
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "Notes");
        var dirId = dir.id();

        noteSteps.createNote(
            token,
            "Deep Learning Neural Networks",
            "Deep learning is a subset of machine learning that uses artificial neural networks with multiple layers. These networks are capable of learning complex patterns in large amounts of data.",
            dirId
        );

        noteSteps.createNote(
            token,
            "Cooking Pasta Recipe",
            "To cook perfect pasta, bring water to a boil, add salt, then add pasta. Cook for 8-10 minutes until al dente. Drain and serve with your favorite sauce.",
            dirId
        );

        noteSteps.createNote(
            token,
            "Neural Network Architectures",
            "Convolutional Neural Networks (CNNs) are specialized for processing grid-like data such as images. Recurrent Neural Networks (RNNs) are designed for sequential data like text or time series.",
            dirId
        );

        // Wait for async indexing to complete
        Thread.sleep(3000);

        // WHEN - User searches for "neural networks"
        var searchResult = searchSteps.search(token, "neural networks", SearchType.SEMANTIC, 10);

        // THEN - Should find the 2 AI-related notes, not the cooking note
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.totalHits()).isEqualTo(2);
        assertThat(searchResult.hits()).hasSize(2);

        // Verify the cooking note is NOT in results
        var titles = searchResult.hits().stream()
            .map(hit -> hit.note().title())
            .toList();
        assertThat(titles).doesNotContain("Cooking Pasta Recipe");
        assertThat(titles).containsExactlyInAnyOrder(
            "Deep Learning Neural Networks",
            "Neural Network Architectures"
        );

        // Verify scores are above threshold
        searchResult.hits().forEach(hit ->
            assertThat(hit.score()).isGreaterThan(0.5)
        );
    }

    @Test
    @SneakyThrows
    void shouldIsolateSearchResultsByUser() {
        // GIVEN - Two users create notes
        var auth1 = userSteps.register("user1@example.com", "password123");
        var token1 = auth1.accessToken();

        var auth2 = userSteps.register("user2@example.com", "password123");
        var token2 = auth2.accessToken();

        var dir1 = dirSteps.createRootDirectory(token1, "User 1 Notes");
        var dir2 = dirSteps.createRootDirectory(token2, "User 2 Notes");

        // User 1 creates a note about Python
        noteSteps.createNote(
            token1,
            "Python Programming Basics",
            "Python is a high-level programming language known for its simplicity and readability. It's widely used in data science, web development, and automation.",
            dir1.id()
        );

        // User 2 creates a note about Java
        noteSteps.createNote(
            token2,
            "Java Enterprise Development",
            "Java is a robust, object-oriented programming language used extensively in enterprise applications. Spring Boot is a popular framework for building Java applications.",
            dir2.id()
        );

        // Wait for async indexing to complete
        Thread.sleep(3000);

        // WHEN - Each user searches for "programming language"
        var searchResult1 = searchSteps.search(token1, "programming language", SearchType.SEMANTIC, 10);
        var searchResult2 = searchSteps.search(token2, "programming language", SearchType.SEMANTIC, 10);

        // THEN - Each user should only see their own note
        assertThat(searchResult1.totalHits()).isEqualTo(1);
        assertThat(searchResult1.hits().get(0).note().title()).isEqualTo("Python Programming Basics");

        assertThat(searchResult2.totalHits()).isEqualTo(1);
        assertThat(searchResult2.hits().get(0).note().title()).isEqualTo("Java Enterprise Development");
    }

    @Test
    @SneakyThrows
    void shouldReturnEmptyResultsWhenNoMatch() {
        // GIVEN - User creates a note about cooking
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "Recipes");
        var dirId = dir.id();

        noteSteps.createNote(
            token,
            "Chocolate Cake Recipe",
            "Mix flour, sugar, cocoa powder, eggs, and milk. Bake at 180°C for 30 minutes. Let cool and frost with chocolate frosting.",
            dirId
        );

        // Wait for async indexing to complete
        Thread.sleep(3000);

        // WHEN - User searches for completely unrelated topic
        var searchResult = searchSteps.search(token, "quantum physics relativity", SearchType.SEMANTIC, 10);

        // THEN - Should return empty results (similarity too low)
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.totalHits()).isEqualTo(0);
        assertThat(searchResult.hits()).isEmpty();
    }

    @Test
    @SneakyThrows
    void shouldFindNotesWithSimilarMeaning() {
        // GIVEN - User creates notes with synonyms/similar concepts
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "Notes");
        var dirId = dir.id();

        noteSteps.createNote(
            token,
            "Automobile Technology",
            "Modern cars are equipped with advanced computer systems, sensors, and automation features. Electric vehicles are becoming increasingly popular.",
            dirId
        );

        noteSteps.createNote(
            token,
            "Software Engineering",
            "Building reliable software requires good design patterns, testing, and version control. Agile methodologies help teams deliver value incrementally.",
            dirId
        );

        // Wait for async indexing to complete
        Thread.sleep(3000);

        // WHEN - User searches using synonym (car instead of automobile)
        var searchResult = searchSteps.search(token, "vehicle transportation car", SearchType.SEMANTIC, 10);

        // THEN - Should find the automobile note based on semantic similarity
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.totalHits()).isEqualTo(1);
        assertThat(searchResult.hits().get(0).note().title()).isEqualTo("Automobile Technology");
    }

    @Test
    @SneakyThrows
    void shouldRespectMaxResultsLimit() {
        // GIVEN - User creates 5 notes about AI/ML
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "AI Notes");
        var dirId = dir.id();

        for (int i = 1; i <= 5; i++) {
            noteSteps.createNote(
                token,
                "AI Topic " + i,
                "This note discusses artificial intelligence and machine learning concepts. Neural networks are powerful tools for pattern recognition.",
                dirId
            );
        }

        // Wait for async indexing to complete
        Thread.sleep(3000);

        // WHEN - User searches with limit of 3
        var searchResult = searchSteps.search(token, "artificial intelligence", SearchType.SEMANTIC, 3);

        // THEN - Should return only 3 results even though 5 match
        assertThat(searchResult).isNotNull();
        assertThat(searchResult.totalHits()).isEqualTo(3);
        assertThat(searchResult.hits()).hasSize(3);
    }

    @Test
    @SneakyThrows
    void shouldUpdateSearchIndexWhenNoteUpdated() {
        // GIVEN - User creates a note about Python
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "Programming");
        var dirId = dir.id();

        var note = noteSteps.createNote(
            token,
            "Python Basics",
            "Python is a programming language used for web development.",
            dirId
        );

        // Wait for async indexing
        Thread.sleep(3000);

        // WHEN - Search finds the Python note
        var searchBefore = searchSteps.search(token, "machine learning", SearchType.SEMANTIC, 10);
        assertThat(searchBefore.totalHits()).isEqualTo(0); // No ML content yet

        // WHEN - Update note to include machine learning content
        noteSteps.updateNote(
            token,
            note.id(),
            "Python for Machine Learning",
            "Python is the most popular programming language for machine learning and data science. Libraries like TensorFlow and PyTorch make building neural networks easy.",
            dirId
        );

        // Wait for async reindexing
        Thread.sleep(3000);

        // THEN - Search should now find the updated note
        var searchAfter = searchSteps.search(token, "machine learning", SearchType.SEMANTIC, 10);
        assertThat(searchAfter.totalHits()).isEqualTo(1);
        assertThat(searchAfter.hits().get(0).note().title()).isEqualTo("Python for Machine Learning");
    }

    @Test
    @SneakyThrows
    void shouldRemoveFromSearchIndexWhenNoteDeleted() {
        // GIVEN - User creates a note
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "Notes");
        var dirId = dir.id();

        var note = noteSteps.createNote(
            token,
            "Temporary Note",
            "This note about artificial intelligence will be deleted soon.",
            dirId
        );

        // Wait for async indexing
        Thread.sleep(3000);

        // WHEN - Verify note is searchable
        var searchBefore = searchSteps.search(token, "artificial intelligence", SearchType.SEMANTIC, 10);
        assertThat(searchBefore.totalHits()).isEqualTo(1);

        // WHEN - Delete the note
        noteSteps.deleteNote(token, note.id());

        // Wait for async index removal
        Thread.sleep(3000);

        // THEN - Note should no longer appear in search results
        var searchAfter = searchSteps.search(token, "artificial intelligence", SearchType.SEMANTIC, 10);
        assertThat(searchAfter.totalHits()).isEqualTo(0);
        assertThat(searchAfter.hits()).isEmpty();
    }
}
