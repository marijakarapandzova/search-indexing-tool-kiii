package mk.ukim.finki.searchindexing.indexing.search;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.searchindexing.config.SearchProperties;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

/**
 * Embedded <a href="https://lucene.apache.org">Apache Lucene</a> implementation
 * of the {@link SearchIndex} seam. The index lives on disk at
 * {@link SearchProperties#indexPath()} (see {@code search.index-path}), so it
 * survives application restarts without needing a separate search cluster.
 *
 * <p>{@code title} is boosted over {@code content} in the relevance query, and
 * {@code macedonianConfidence} is additionally stored as a {@link DoublePoint}
 * so {@link SearchQuery#minMacedonianConfidence()} can be applied as a fast
 * range filter alongside the free-text query.</p>
 */
@Component
@Slf4j
public class LuceneSearchIndex implements SearchIndex {
    private static final String FIELD_DOC_ID = "docId";
    private static final String FIELD_URL = "url";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_CONFIDENCE = "macedonianConfidence";
    private static final String FIELD_CONFIDENCE_POINT = "macedonianConfidencePoint";

    private static final int SNIPPET_RADIUS = 120;

    private final SearchProperties searchProperties;
    private final Analyzer analyzer = new StandardAnalyzer();

    private Directory directory;
    private IndexWriter indexWriter;

    public LuceneSearchIndex(SearchProperties searchProperties) {
        this.searchProperties = searchProperties;
    }

    @PostConstruct
    public void open() throws IOException {
        String indexPath = searchProperties.indexPath() == null
            ? "./data/search-index"
            : searchProperties.indexPath();
        Path path = Path.of(indexPath);
        Files.createDirectories(path);
        directory = FSDirectory.open(path);
        indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        indexWriter.commit();
        log.info("Lucene search index opened at {}", path.toAbsolutePath());
    }

    @PreDestroy
    public void close() {
        try {
            if (indexWriter != null) {
                indexWriter.close();
            }
            if (directory != null) {
                directory.close();
            }
        } catch (IOException exception) {
            log.warn("Failed to close the Lucene search index cleanly: {}", exception.getMessage());
        }
    }

    @Override
    public synchronized void index(IndexDocument document) {
        try {
            Document luceneDocument = new Document();
            luceneDocument.add(new StringField(FIELD_DOC_ID, document.docId(), Field.Store.YES));
            luceneDocument.add(new StringField(FIELD_URL, document.url(), Field.Store.YES));
            luceneDocument.add(new TextField(FIELD_TITLE, safe(document.title()), Field.Store.YES));
            luceneDocument.add(new TextField(FIELD_CONTENT, safe(document.content()), Field.Store.YES));

            double confidence = document.macedonianConfidence() == null ? 0.0 : document.macedonianConfidence();
            luceneDocument.add(new DoublePoint(FIELD_CONFIDENCE_POINT, confidence));
            luceneDocument.add(new StoredField(FIELD_CONFIDENCE, String.valueOf(confidence)));

            indexWriter.updateDocument(new Term(FIELD_DOC_ID, document.docId()), luceneDocument);
            indexWriter.commit();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to index document " + document.docId(), exception);
        }
    }

    @Override
    public SearchResult search(SearchQuery query) {
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            Query textQuery = buildTextQuery(query.query());
            Query finalQuery = query.minMacedonianConfidence() == null
                ? textQuery
                : new BooleanQuery.Builder()
                    .add(textQuery, BooleanClause.Occur.MUST)
                    .add(
                        DoublePoint.newRangeQuery(FIELD_CONFIDENCE_POINT, query.minMacedonianConfidence(), Double.MAX_VALUE),
                        BooleanClause.Occur.FILTER
                    )
                    .build();

            int page = Math.max(query.page(), 0);
            int size = query.size() <= 0 ? 10 : query.size();
            int upperBound = (page + 1) * size;

            TopDocs topDocs = searcher.search(finalQuery, upperBound == 0 ? size : upperBound);
            List<SearchHit> hits = new ArrayList<>();

            int start = page * size;
            int end = Math.min(topDocs.scoreDocs.length, start + size);
            for (int i = start; i < end; i++) {
                ScoreDoc scoreDoc = topDocs.scoreDocs[i];
                Document document = searcher.storedFields().document(scoreDoc.doc);
                hits.add(toSearchHit(document, scoreDoc.score, query.query()));
            }

            return new SearchResult(query.query(), topDocs.totalHits.value, hits);
        } catch (IOException | ParseException exception) {
            throw new RuntimeException("Search failed for query '" + query.query() + "'", exception);
        }
    }

    @Override
    public synchronized void deleteById(String docId) {
        try {
            indexWriter.deleteDocuments(new Term(FIELD_DOC_ID, docId));
            indexWriter.commit();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to delete document " + docId, exception);
        }
    }

    @Override
    public synchronized void deleteAll() {
        try {
            indexWriter.deleteAll();
            indexWriter.commit();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to clear the search index", exception);
        }
    }

    @Override
    public long documentCount() {
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            return reader.numDocs();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to count documents in the search index", exception);
        }
    }

    private Query buildTextQuery(String rawQuery) throws ParseException {
        Map<String, Float> boosts = new HashMap<>();
        boosts.put(FIELD_TITLE, 2.0f);
        boosts.put(FIELD_CONTENT, 1.0f);
        MultiFieldQueryParser parser =
            new MultiFieldQueryParser(new String[] {FIELD_TITLE, FIELD_CONTENT}, analyzer, boosts);
        parser.setAllowLeadingWildcard(false);
        String escaped = QueryParser.escape(rawQuery == null ? "" : rawQuery.trim());
        if (escaped.isBlank()) {
            escaped = "*";
            parser.setAllowLeadingWildcard(true);
        }
        return parser.parse(escaped);
    }

    private SearchHit toSearchHit(Document document, double score, String query) {
        String content = document.get(FIELD_CONTENT);
        String confidenceValue = document.get(FIELD_CONFIDENCE);
        return new SearchHit(
            document.get(FIELD_DOC_ID),
            document.get(FIELD_URL),
            document.get(FIELD_TITLE),
            buildSnippet(content, query),
            score,
            confidenceValue == null ? null : Double.valueOf(confidenceValue)
        );
    }

    private String buildSnippet(String content, String query) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String lowerContent = content.toLowerCase();
        int matchIndex = -1;
        if (query != null) {
            for (String term : query.toLowerCase().split("\\s+")) {
                if (term.length() < 2) {
                    continue;
                }
                int index = lowerContent.indexOf(term);
                if (index >= 0) {
                    matchIndex = index;
                    break;
                }
            }
        }
        if (matchIndex < 0) {
            return content.length() <= 2 * SNIPPET_RADIUS
                ? content
                : content.substring(0, 2 * SNIPPET_RADIUS).trim() + "...";
        }
        int start = Math.max(0, matchIndex - SNIPPET_RADIUS);
        int end = Math.min(content.length(), matchIndex + SNIPPET_RADIUS);
        String snippet = content.substring(start, end).trim();
        return (start > 0 ? "..." : "") + snippet + (end < content.length() ? "..." : "");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
