package mk.ukim.finki.searchindexing.service.application;

import java.util.Optional;
import mk.ukim.finki.searchindexing.model.dto.DisplayIndexedDocumentDto;
import mk.ukim.finki.searchindexing.model.dto.DocumentFilterDto;
import mk.ukim.finki.searchindexing.model.dto.SearchQueryDto;
import mk.ukim.finki.searchindexing.model.dto.SearchResultDto;
import org.springframework.data.domain.Page;

/**
 * Application service for browsing and searching the indexed content.
 */
public interface IndexedDocumentApplicationService {
    Page<DisplayIndexedDocumentDto> findAll(DocumentFilterDto filter, int page, int size);

    Optional<DisplayIndexedDocumentDto> findById(Long id);

    Optional<DisplayIndexedDocumentDto> deleteById(Long id);

    /**
     * Full-text search over the search index.
     */
    SearchResultDto search(SearchQueryDto searchQueryDto);
}
