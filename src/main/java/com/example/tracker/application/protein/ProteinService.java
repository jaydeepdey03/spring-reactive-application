package com.example.tracker.application.protein;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.tracker.domain.protein.DailyProteinSummary;
import com.example.tracker.domain.protein.ProteinEntry;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProteinService {
    Mono<ProteinEntry> addEntry(UUID userId, String foodName, BigDecimal gramsConsumed, BigDecimal proteinGrams,
            LocalDate date);

    Mono<ProteinEntry> updateEntry(UUID userId, UUID entryId, BigDecimal newProteinGrams);

    Mono<Void> deleteEntry(UUID userId, UUID entryId);

    Flux<ProteinEntry> listForDate(UUID userId, LocalDate date);

    Mono<DailyProteinSummary> getDailySummary(UUID userId, LocalDate date);
}
