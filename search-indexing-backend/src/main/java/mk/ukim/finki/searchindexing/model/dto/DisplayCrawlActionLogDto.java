package mk.ukim.finki.searchindexing.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.CrawlActionLog;
import mk.ukim.finki.searchindexing.model.enums.CrawlActionType;

public record DisplayCrawlActionLogDto(
    Long id,
    CrawlActionType actionType,
    String details,
    Boolean successful,
    LocalDateTime occurredAt
) {
    public static DisplayCrawlActionLogDto from(CrawlActionLog log) {
        return new DisplayCrawlActionLogDto(
            log.getId(),
            log.getActionType(),
            log.getDetails(),
            log.getSuccessful(),
            log.getOccurredAt()
        );
    }

    public static List<DisplayCrawlActionLogDto> from(List<CrawlActionLog> logs) {
        return logs
            .stream()
            .map(DisplayCrawlActionLogDto::from)
            .toList();
    }
}
