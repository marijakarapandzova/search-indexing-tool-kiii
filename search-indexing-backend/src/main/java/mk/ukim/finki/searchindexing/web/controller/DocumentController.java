package mk.ukim.finki.searchindexing.web.controller;

import mk.ukim.finki.searchindexing.model.dto.DisplayIndexedDocumentDto;
import mk.ukim.finki.searchindexing.model.dto.DocumentFilterDto;
import mk.ukim.finki.searchindexing.model.dto.SearchQueryDto;
import mk.ukim.finki.searchindexing.model.dto.SearchResultDto;
import mk.ukim.finki.searchindexing.model.enums.ResourceType;
import mk.ukim.finki.searchindexing.service.application.IndexedDocumentApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The indexed-content API: full-text search over the search index, plus paged,
 * filterable browsing of everything the pipeline has indexed.
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final IndexedDocumentApplicationService indexedDocumentApplicationService;

    public DocumentController(IndexedDocumentApplicationService indexedDocumentApplicationService) {
        this.indexedDocumentApplicationService = indexedDocumentApplicationService;
    }

    /**
     * Full-text search — the headline feature. Queries the search engine seam
     * rather than the relational store.
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResultDto> search(
        @RequestParam String query,
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(required = false) Double minMacedonianConfidence
    ) {
        SearchQueryDto searchQueryDto = new SearchQueryDto(query, page, size, minMacedonianConfidence);
        return ResponseEntity.ok(indexedDocumentApplicationService.search(searchQueryDto));
    }

    @GetMapping
    public ResponseEntity<Page<DisplayIndexedDocumentDto>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Long jobId,
        @RequestParam(required = false) ResourceType resourceType,
        @RequestParam(required = false) Double minMacedonianConfidence,
        @RequestParam(required = false) Boolean donated,
        @RequestParam(required = false) String search
    ) {
        DocumentFilterDto filter =
            new DocumentFilterDto(jobId, resourceType, minMacedonianConfidence, donated, search);
        return ResponseEntity.ok(indexedDocumentApplicationService.findAll(filter, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisplayIndexedDocumentDto> findById(@PathVariable Long id) {
        return indexedDocumentApplicationService
            .findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<DisplayIndexedDocumentDto> deleteById(@PathVariable Long id) {
        return indexedDocumentApplicationService
            .deleteById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
