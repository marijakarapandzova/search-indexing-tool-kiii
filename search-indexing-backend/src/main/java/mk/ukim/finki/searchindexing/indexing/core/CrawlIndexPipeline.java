package mk.ukim.finki.searchindexing.indexing.core;

import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.CrawlSeed;
import mk.ukim.finki.searchindexing.model.domain.IndexingJob;
import mk.ukim.finki.searchindexing.model.dto.CreateIndexedDocumentDto;

/**
 * The crawl-and-index pipeline for the assigned website.
 *
 * <p>Each student implements this for their website by extending
 * {@link AbstractCrawlIndexPipeline}, which already contains the generic
 * frontier loop (fetch → parse → detect language → index → enqueue links). The
 * template assumes exactly one {@code CrawlIndexPipeline} bean in the
 * application context.</p>
 */
public interface CrawlIndexPipeline {
    /**
     * Runs the crawl-index loop starting from the given seeds and returns the
     * documents that were fetched, parsed and added to the search index (each
     * annotated with a Macedonian-language confidence). Does not persist to the
     * database — the caller decides what to do with the result.
     */
    List<CreateIndexedDocumentDto> execute(IndexingJob job, List<CrawlSeed> seeds, CrawlStepListener stepListener);

    /**
     * Releases any resources held by the pipeline (HTTP client, browser, ...).
     */
    void shutdown();
}
