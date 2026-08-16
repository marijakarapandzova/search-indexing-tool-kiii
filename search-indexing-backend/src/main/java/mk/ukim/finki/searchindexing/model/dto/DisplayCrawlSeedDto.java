package mk.ukim.finki.searchindexing.model.dto;

import java.util.List;
import mk.ukim.finki.searchindexing.model.domain.CrawlSeed;
import mk.ukim.finki.searchindexing.model.enums.SeedType;

public record DisplayCrawlSeedDto(
    Long id,
    SeedType type,
    String value
) {
    public static DisplayCrawlSeedDto from(CrawlSeed seed) {
        return new DisplayCrawlSeedDto(
            seed.getId(),
            seed.getType(),
            seed.getValue()
        );
    }

    public static List<DisplayCrawlSeedDto> from(List<CrawlSeed> seeds) {
        return seeds
            .stream()
            .map(DisplayCrawlSeedDto::from)
            .toList();
    }
}
