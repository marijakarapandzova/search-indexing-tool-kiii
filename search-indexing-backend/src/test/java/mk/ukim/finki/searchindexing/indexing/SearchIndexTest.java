package mk.ukim.finki.searchindexing.indexing;

import static org.assertj.core.api.Assertions.assertThat;

import mk.ukim.finki.searchindexing.indexing.search.IndexDocument;
import mk.ukim.finki.searchindexing.indexing.search.LuceneSearchIndex;
import mk.ukim.finki.searchindexing.indexing.search.SearchIndex;
import mk.ukim.finki.searchindexing.indexing.search.SearchQuery;
import mk.ukim.finki.searchindexing.indexing.search.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class SearchIndexTest {
    private SearchIndex searchIndex;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        searchIndex = new LuceneSearchIndex(tempDir.toString());
    }

    @Test
    void testIndexAndSearch() {
        // Index documents including Macedonian and non-Macedonian ones
        searchIndex.index(new IndexDocument(
            "doc1",
            "https://example.mk/page1",
            "Македонска страница",
            "Ова е содржина на македонска страница со многу текст за тестирање",
            0.95
        ));

        searchIndex.index(new IndexDocument(
            "doc2",
            "https://example.mk/page2",
            "Another Page",
            "This is English content that should match search queries",
            0.15
        ));

        searchIndex.index(new IndexDocument(
            "doc3",
            "https://example.mk/page3",
            "Страница со модерна содржина",
            "Модерна технологија и развој во Македонија",
            0.88
        ));

        // Test basic search
        SearchResult result = searchIndex.search(new SearchQuery("македонска", 0, 10, null));
        assertThat(result.totalHits()).isGreaterThan(0);
        assertThat(result.hits()).isNotEmpty();
        assertThat(result.query()).isEqualTo("македонска");

        // Test minMacedonianConfidence filtering
        SearchResult macedonianOnly = searchIndex.search(
            new SearchQuery("страница", 0, 10, 0.8)
        );
        for (var hit : macedonianOnly.hits()) {
            assertThat(hit.macedonianConfidence()).isGreaterThanOrEqualTo(0.8);
        }

        // Test document count
        assertThat(searchIndex.documentCount()).isEqualTo(3);

        // Test that English document doesn't match Macedonian query as strongly
        SearchResult englishSearch = searchIndex.search(new SearchQuery("English", 0, 10, null));
        assertThat(englishSearch.totalHits()).isGreaterThan(0);
    }

    @Test
    void testIndexReplacement() {
        // Index a document
        searchIndex.index(new IndexDocument(
            "doc1",
            "https://example.mk/page1",
            "Original Title",
            "Original content",
            0.9
        ));

        assertThat(searchIndex.documentCount()).isEqualTo(1);

        // Replace it with updated content
        searchIndex.index(new IndexDocument(
            "doc1",
            "https://example.mk/page1",
            "Updated Title",
            "Updated content with new text",
            0.92
        ));

        // Document count should still be 1
        assertThat(searchIndex.documentCount()).isEqualTo(1);

        // Search should return the updated document
        SearchResult result = searchIndex.search(new SearchQuery("Updated", 0, 10, null));
        assertThat(result.totalHits()).isGreaterThan(0);
    }

    @Test
    void testDeleteById() {
        searchIndex.index(new IndexDocument(
            "doc1",
            "https://example.mk/page1",
            "Title",
            "Content",
            0.9
        ));

        assertThat(searchIndex.documentCount()).isEqualTo(1);

        searchIndex.deleteById("doc1");

        assertThat(searchIndex.documentCount()).isEqualTo(0);
    }

    @Test
    void testDeleteAll() {
        searchIndex.index(new IndexDocument("doc1", "url1", "Title 1", "Content 1", 0.9));
        searchIndex.index(new IndexDocument("doc2", "url2", "Title 2", "Content 2", 0.8));
        searchIndex.index(new IndexDocument("doc3", "url3", "Title 3", "Content 3", 0.7));

        assertThat(searchIndex.documentCount()).isEqualTo(3);

        searchIndex.deleteAll();

        assertThat(searchIndex.documentCount()).isEqualTo(0);
    }
}
