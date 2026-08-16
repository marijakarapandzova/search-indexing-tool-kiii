package mk.ukim.finki.searchindexing.indexing.parser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import mk.ukim.finki.searchindexing.indexing.crawler.FetchedPage;
import mk.ukim.finki.searchindexing.model.dto.CreateMediaItemDto;
import mk.ukim.finki.searchindexing.model.enums.MediaType;
import mk.ukim.finki.searchindexing.model.enums.ResourceType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * Parses pages of <a href="https://360stepeni.mk">360stepeni.mk</a>, a
 * WordPress-based Macedonian news outlet. WordPress themes vary in their exact
 * markup, so the article-body extraction tries a small list of well-known
 * WordPress content selectors before falling back to "all paragraph text",
 * which keeps the parser robust to theme differences and future redesigns.
 */
@Component
public class Stepeni360DocumentParser implements DocumentParser {
    /** Tried in order; the first selector that yields substantial text wins. */
    private static final String[] CONTENT_SELECTORS = {
        "div.entry-content",
        "article .entry-content",
        "div.td-post-content",
        "div.post-content",
        "div.single-post-content",
        "article",
        "main"
    };

    private static final int MIN_CONTENT_LENGTH = 200;
    private static final int MAX_MEDIA_ITEMS = 20;

    @Override
    public ParsedDocument parse(FetchedPage page) {
        Document document = Jsoup.parse(page.html(), page.url());

        String title = extractTitle(document);
        Element contentElement = findContentElement(document);
        String text = contentElement == null ? extractFallbackText(document) : cleanText(contentElement);

        boolean isListingPage = page.url().contains("/category/")
            || page.url().contains("/tag/")
            || page.url().matches(".*/page/\\d+/?$");

        ResourceType resourceType = isListingPage
            ? ResourceType.PAGE
            : (contentElement != null && text.length() >= MIN_CONTENT_LENGTH ? ResourceType.ARTICLE : ResourceType.PAGE);

        List<String> links = extractLinks(document);
        List<CreateMediaItemDto> mediaItems = extractMedia(contentElement != null ? contentElement : document.body());

        return new ParsedDocument(title, text, resourceType, links, mediaItems);
    }

    private String extractTitle(Document document) {
        String ogTitle = document.select("meta[property=og:title]").attr("content");
        if (!ogTitle.isBlank()) {
            return ogTitle.trim();
        }
        Element headline = document.selectFirst("h1.entry-title, h1.post-title, article h1, h1");
        if (headline != null && !headline.text().isBlank()) {
            return headline.text().trim();
        }
        String docTitle = document.title();
        if (docTitle == null) {
            return "";
        }
        // WordPress titles are usually "Article headline - 360 степени"; drop the site suffix.
        int dashIndex = docTitle.lastIndexOf(" - 360");
        return (dashIndex > 0 ? docTitle.substring(0, dashIndex) : docTitle).trim();
    }

    private Element findContentElement(Document document) {
        for (String selector : CONTENT_SELECTORS) {
            Element candidate = document.selectFirst(selector);
            if (candidate != null && cleanText(candidate).length() >= MIN_CONTENT_LENGTH) {
                return candidate;
            }
        }
        // Last resort: any of the tried selectors, even with little text (e.g. short pages).
        for (String selector : CONTENT_SELECTORS) {
            Element candidate = document.selectFirst(selector);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    private String extractFallbackText(Document document) {
        Elements paragraphs = document.select("p");
        StringBuilder builder = new StringBuilder();
        for (Element paragraph : paragraphs) {
            String text = paragraph.text().trim();
            if (!text.isEmpty()) {
                builder.append(text).append("\n\n");
            }
        }
        return builder.toString().trim();
    }

    private String cleanText(Element element) {
        // Strip boilerplate that WordPress themes commonly embed inside the content area.
        Element clone = element.clone();
        clone.select("script, style, nav, .sharedaddy, .jp-relatedposts, .wp-block-buttons, form, iframe")
            .remove();
        return clone.text().trim();
    }

    private List<String> extractLinks(Document document) {
        Set<String> links = new LinkedHashSet<>();
        for (Element anchor : document.select("a[href]")) {
            String href = anchor.absUrl("href");
            if (!href.isBlank()) {
                links.add(href);
            }
        }
        return new ArrayList<>(links);
    }

    private List<CreateMediaItemDto> extractMedia(Element scope) {
        List<CreateMediaItemDto> mediaItems = new ArrayList<>();
        if (scope == null) {
            return mediaItems;
        }
        Set<String> seen = new LinkedHashSet<>();
        for (Element image : scope.select("img[src]")) {
            String src = image.absUrl("src");
            if (src.isBlank() || !seen.add(src) || mediaItems.size() >= MAX_MEDIA_ITEMS) {
                continue;
            }
            mediaItems.add(new CreateMediaItemDto(MediaType.IMAGE, src, null));
        }
        for (Element iframe : scope.select("iframe[src]")) {
            String src = iframe.absUrl("src");
            if (src.isBlank() || !seen.add(src) || mediaItems.size() >= MAX_MEDIA_ITEMS) {
                continue;
            }
            if (src.contains("youtube") || src.contains("vimeo") || src.contains("video")) {
                mediaItems.add(new CreateMediaItemDto(MediaType.VIDEO, src, null));
            }
        }
        return mediaItems;
    }
}
