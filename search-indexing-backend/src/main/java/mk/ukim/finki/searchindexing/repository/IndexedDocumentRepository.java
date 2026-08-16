package mk.ukim.finki.searchindexing.repository;

import mk.ukim.finki.searchindexing.model.domain.IndexedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexedDocumentRepository
    extends JpaRepository<IndexedDocument, Long>, JpaSpecificationExecutor<IndexedDocument> {
    // TODO(student): Implement filtering for DocumentFilterDto, e.g. with JPA
    //  Specifications (the JpaSpecificationExecutor above) or custom @Query methods.
}
