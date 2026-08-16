package mk.ukim.finki.searchindexing.service.application.impl;

import java.util.Optional;
import mk.ukim.finki.searchindexing.model.dto.DisplayIndexedDocumentDto;
import mk.ukim.finki.searchindexing.model.dto.DocumentFilterDto;
import mk.ukim.finki.searchindexing.model.dto.SearchQueryDto;
import mk.ukim.finki.searchindexing.model.dto.SearchResultDto;
import mk.ukim.finki.searchindexing.service.application.IndexedDocumentApplicationService;
import mk.ukim.finki.searchindexing.service.domain.IndexedDocumentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class IndexedDocumentApplicationServiceImpl implements IndexedDocumentApplicationService {
    private final IndexedDocumentService indexedDocumentService;

    public IndexedDocumentApplicationServiceImpl(IndexedDocumentService indexedDocumentService) {
        this.indexedDocumentService = indexedDocumentService;
    }

    @Override
    public Page<DisplayIndexedDocumentDto> findAll(DocumentFilterDto filter, int page, int size) {
        return indexedDocumentService.findAll(filter, page, size).map(DisplayIndexedDocumentDto::from);
    }

    @Override
    public Optional<DisplayIndexedDocumentDto> findById(Long id) {
        return indexedDocumentService.findById(id).map(DisplayIndexedDocumentDto::from);
    }

    @Override
    public Optional<DisplayIndexedDocumentDto> deleteById(Long id) {
        return indexedDocumentService.deleteById(id).map(DisplayIndexedDocumentDto::from);
    }

    @Override
    public SearchResultDto search(SearchQueryDto searchQueryDto) {
        return SearchResultDto.from(indexedDocumentService.search(searchQueryDto.toSearchQuery()));
    }
}
