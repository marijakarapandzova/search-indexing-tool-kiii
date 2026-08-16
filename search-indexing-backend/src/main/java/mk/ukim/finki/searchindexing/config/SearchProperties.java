package mk.ukim.finki.searchindexing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration of the local search engine, bound from the {@code search.*}
 * properties.
 *
 * <p>Which of these you actually use depends on the engine you pick for the
 * {@code SearchIndex} seam. For an embedded Lucene index {@code indexPath} is
 * the directory the index lives in; for a remote Elasticsearch/OpenSearch
 * cluster you would use {@code uri} and {@code indexName} instead.</p>
 *
 * @param indexPath filesystem location of an embedded index (e.g. Lucene)
 * @param uri       base URI of a remote search cluster, when applicable
 * @param indexName the name of the index/collection to read and write
 */
@ConfigurationProperties(prefix = "search")
public record SearchProperties(
    String indexPath,
    String uri,
    String indexName
) {
}
