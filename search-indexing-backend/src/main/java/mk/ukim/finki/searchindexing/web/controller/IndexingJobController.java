package mk.ukim.finki.searchindexing.web.controller;

import jakarta.validation.Valid;
import java.util.List;
import mk.ukim.finki.searchindexing.model.dto.CreateIndexingJobDto;
import mk.ukim.finki.searchindexing.model.dto.DisplayCrawlActionLogDto;
import mk.ukim.finki.searchindexing.model.dto.DisplayIndexingJobDto;
import mk.ukim.finki.searchindexing.service.application.IndexingJobApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The indexing control panel API: create indexing jobs, start/stop them and
 * follow what the crawl-index pipeline did.
 */
@RestController
@RequestMapping("/api/jobs")
public class IndexingJobController {
    private final IndexingJobApplicationService indexingJobApplicationService;

    public IndexingJobController(IndexingJobApplicationService indexingJobApplicationService) {
        this.indexingJobApplicationService = indexingJobApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<DisplayIndexingJobDto>> findAll() {
        return ResponseEntity.ok(indexingJobApplicationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisplayIndexingJobDto> findById(@PathVariable Long id) {
        return indexingJobApplicationService
            .findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<DisplayIndexingJobDto> create(
        @RequestBody @Valid CreateIndexingJobDto createIndexingJobDto
    ) {
        return ResponseEntity.ok(indexingJobApplicationService.create(createIndexingJobDto));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<DisplayIndexingJobDto> start(@PathVariable Long id) {
        return ResponseEntity.ok(indexingJobApplicationService.start(id));
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<DisplayIndexingJobDto> stop(@PathVariable Long id) {
        return ResponseEntity.ok(indexingJobApplicationService.stop(id));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<DisplayCrawlActionLogDto>> findLogs(@PathVariable Long id) {
        return ResponseEntity.ok(indexingJobApplicationService.findLogsByJobId(id));
    }
}
