package mk.ukim.finki.searchindexing.indexing.core;

import mk.ukim.finki.searchindexing.model.enums.CrawlActionType;

/**
 * Callback invoked by the crawl-index loop after every step, so the caller
 * (the {@code IndexingOrchestrator}) can persist a {@code CrawlActionLog}
 * without the indexing layer depending on services or repositories.
 */
@FunctionalInterface
public interface CrawlStepListener {
    void onStep(CrawlActionType actionType, String details, boolean successful);
}
