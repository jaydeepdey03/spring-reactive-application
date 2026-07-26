package com.example.tracker.domain.expense;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface DailyExpenseSummaryRepository extends ReactiveCrudRepository<DailyExpenseSummary, UUID> {
    Mono<DailyExpenseSummary> findByUserIdAndSummaryDate(UUID userId, LocalDate summaryDate);
}
