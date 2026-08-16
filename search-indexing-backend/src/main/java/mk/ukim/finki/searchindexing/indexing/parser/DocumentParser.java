package mk.ukim.finki.searchindexing.indexing.parser;

import mk.ukim.finki.searchindexing.indexing.crawler.FetchedPage;

/**
 * Turns a fetched page into a structured, searchable {@link ParsedDocument}.
 *
 * <p>TODO(student): Provide an implementation for your assigned website. It
 * typically parses the HTML (e.g. with JSoup), strips boilerplate (navigation,
 * footers, scripts), extracts the main title and text, collects outgoing links
 * for the crawl frontier and any media. The returned document does not need a
 * language confidence — the crawl-index loop fills it in via the
 * {@link LanguageDetector}.</p>
 */
public interface DocumentParser {
    ParsedDocument parse(FetchedPage page);
}
