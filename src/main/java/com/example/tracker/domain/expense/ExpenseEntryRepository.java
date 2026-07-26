package com.example.tracker.domain.expense;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface ExpenseEntryRepository extends ReactiveCrudRepository<ExpenseEntry, UUID> {
    Flux<ExpenseEntry> findByUserIdAndEntryDateOrderByCreatedAtDesc(UUID userId, LocalDate entryDate);

    Flux<ExpenseEntry> findByUserIdAndEntryDateBetweenOrderByEntryDateDesc(
            UUID userId, LocalDate from, LocalDate to);

    Flux<ExpenseEntry> findByUserIdAndCategoryAndEntryDateBetween(
            UUID userId, String category, LocalDate from, LocalDate to);

}
