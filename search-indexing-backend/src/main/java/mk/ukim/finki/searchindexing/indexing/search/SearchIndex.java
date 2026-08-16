package mk.ukim.finki.searchindexing.indexing.search;

/**
 * The heart of the project: the local search engine seam. Everything that adds
 * content to, or queries, the index goes through this interface, so the rest of
 * the application does not depend on which engine you choose.
 *
 * <p>TODO(student): Provide an implementation backed by a search engine of your
 * choice — <b>Apache Lucene</b> (embedded), <b>Elasticsearch</b>/<b>OpenSearch</b>
 * (remote), or a comparable library. <b>Add the dependency yourself</b> in
 * {@code pom.xml} and configure it via {@code SearchProperties}. The crawl-index
 * pipeline calls {@link #index(IndexDocument)} for every page it harvests, and
 * the document search endpoint calls {@link #search(SearchQuery)}.</p>
 */
public interface SearchIndex {
    /**
     * Adds a document to the index, replacing any existing document with the
     * same {@link IndexDocument#docId()}.
     */
    void index(IndexDocument document);

    /**
     * Runs a full-text query and returns the matching page of hits.
     */
    SearchResult search(SearchQuery query);

    /**
     * Removes the document with the given id from the index, if present.
     */
    void deleteById(String docId);

    /**
     * Clears the whole index.
     */
    void deleteAll();

    /**
     * @return the number of documents currently in the index
     */
    long documentCount();
}
