package mk.ukim.finki.searchindexing.indexing.parser;

import java.util.List;
import mk.ukim.finki.searchindexing.model.dto.CreateMediaItemDto;
import mk.ukim.finki.searchindexing.model.enums.ResourceType;

/**
 * The structured result of parsing a {@code FetchedPage}: the searchable
 * content plus the links the crawl-index loop should consider following.
 *
 * @param title           the page/resource title
 * @param textContent     the clean, human-readable text to be indexed
 * @param resourceType    what kind of resource this is
 * @param discoveredLinks absolute URLs found on the page, candidates for the
 *                        crawl frontier (the loop decides which to follow)
 * @param mediaItems      images/videos/files found on the page
 */
public record ParsedDocument(
    String title,
    String textContent,
    ResourceType resourceType,
    List<String> discoveredLinks,
    List<CreateMediaItemDto> mediaItems
) {
}
