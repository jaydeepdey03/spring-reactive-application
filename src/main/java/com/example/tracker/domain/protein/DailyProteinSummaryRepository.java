package com.example.tracker.domain.protein;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface DailyProteinSummaryRepository extends ReactiveCrudRepository<DailyProteinSummary, UUID> {
    Mono<DailyProteinSummary> findByUserIdAndSummaryDate(UUID userId, LocalDate summaryDate);

}
