package mk.ukim.finki.searchindexing.indexing.search;

/**
 * A single document to add to (or update in) the search index.
 *
 * @param docId               stable identifier of the document in the index
 *                            (adding one with an existing id replaces it)
 * @param url                 the source URL, returned with search hits
 * @param title               the document title
 * @param content             the full searchable text
 * @param macedonianConfidence language confidence, so the engine can boost or
 *                            filter by "how Macedonian" a document is
 */
public record IndexDocument(
    String docId,
    String url,
    String title,
    String content,
    Double macedonianConfidence
) {
}
