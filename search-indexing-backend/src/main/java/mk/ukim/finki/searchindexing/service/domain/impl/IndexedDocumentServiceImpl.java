package mk.ukim.finki.searchindexing.service.domain.impl;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mk.ukim.finki.searchindexing.indexing.search.SearchIndex;
import mk.ukim.finki.searchindexing.indexing.search.SearchQuery;
import mk.ukim.finki.searchindexing.indexing.search.SearchResult;
import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import mk.ukim.finki.searchindexing.model.dto.DocumentFilterDto;
import mk.ukim.finki.searchindexing.repository.IndexedDocumentRepository;
import mk.ukim.finki.searchindexing.service.domain.IndexedDocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndexedDocumentServiceImpl implements IndexedDocumentService {
    private final IndexedDocumentRepository indexedDocumentRepository;
    private final SearchIndex searchIndex;

    public IndexedDocumentServiceImpl(
        IndexedDocumentRepository indexedDocumentRepository,
        SearchIndex searchIndex
    ) {
        this.indexedDocumentRepository = indexedDocumentRepository;
        this.searchIndex = searchIndex;
    }

    @Override
    public Page<IndexedDocument> findAll(DocumentFilterDto filter, int page, int size) {
        return indexedDocumentRepository.findAll(toSpecification(filter), PageRequest.of(page, size));
    }

    @Override
    public Optional<IndexedDocument> findById(Long id) {
        return indexedDocumentRepository.findById(id);
    }

    @Override
    public List<IndexedDocument> findAllById(List<Long> ids) {
        return indexedDocumentRepository.findAllById(ids);
    }

    @Override
    public List<IndexedDocument> saveAll(List<IndexedDocument> documents) {
        return indexedDocumentRepository.saveAll(documents);
    }

    @Override
    @Transactional
    public Optional<IndexedDocument> deleteById(Long id) {
        Optional<IndexedDocument> document = indexedDocumentRepository.findById(id);
        document.ifPresent(existing -> {
            indexedDocumentRepository.deleteById(id);
            searchIndex.deleteById(existing.getDocId());
        });
        return document;
    }

    @Override
    public SearchResult search(SearchQuery query) {
        return searchIndex.search(query);
    }

    private Specification<IndexedDocument> toSpecification(DocumentFilterDto filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.jobId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("job").get("id"), filter.jobId()));
            }
            if (filter.resourceType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("resourceType"), filter.resourceType()));
            }
            if (filter.minMacedonianConfidence() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("macedonianConfidence"), filter.minMacedonianConfidence()));
            }
            if (filter.donated() != null) {
                predicates.add(filter.donated()
                    ? criteriaBuilder.isNotNull(root.get("donationBatch"))
                    : criteriaBuilder.isNull(root.get("donationBatch")));
            }
            if (filter.search() != null && !filter.search().isBlank()) {
                String pattern = "%" + filter.search().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), pattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
