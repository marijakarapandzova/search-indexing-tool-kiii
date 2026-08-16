package mk.ukim.finki.searchindexing.service.domain;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.searchindexing.indexing.search.SearchQuery;
import mk.ukim.finki.searchindexing.indexing.search.SearchResult;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.dto.DocumentFilterDto;
import org.springframework.data.domain.Page;

/**
 * Domain service for indexed documents: both the relational browsing view
 * (paged/filtered over the database) and the full-text search view (over the
 * {@code SearchIndex}).
 */
public interface IndexedDocumentService {
    /**
     * Paged browsing of indexed documents. Every filter field may be
     * {@code null} — see {@link DocumentFilterDto}.
     */
    Page<IndexedDocument> findAll(DocumentFilterDto filter, int page, int size);

    Optional<IndexedDocument> findById(Long id);

    List<IndexedDocument> findAllById(List<Long> ids);

    /**
     * Persists the documents a crawl-index run produced.
     */
    List<IndexedDocument> saveAll(List<IndexedDocument> documents);

    Optional<IndexedDocument> deleteById(Long id);

    /**
     * Full-text search over the {@code SearchIndex} seam.
     */
    SearchResult search(SearchQuery query);
}
