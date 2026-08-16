package mk.ukim.finki.searchindexing.indexing.search;

/**
 * One result of a search: enough to render it and link back to the source.
 *
 * @param docId                the id of the matching document in the index
 * @param url                  the document's source URL
 * @param title                the document title
 * @param snippet              a short highlighted excerpt around the match
 * @param score                the engine's relevance score for this hit
 * @param macedonianConfidence the document's language confidence
 */
public record SearchHit(
    String docId,
    String url,
    String title,
    String snippet,
    double score,
    Double macedonianConfidence
) {
}
