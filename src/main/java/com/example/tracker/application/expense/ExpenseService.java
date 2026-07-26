package com.example.tracker.application.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.tracker.domain.expense.DailyExpenseSummary;
import com.example.tracker.domain.expense.ExpenseEntry;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExpenseService {
    Mono<ExpenseEntry> addEntry(UUID userId, String description, String category, BigDecimal amount, String currency,
            LocalDate date);

    Mono<ExpenseEntry> updateEntry(UUID userId, UUID entryId, BigDecimal newAmount);

    Mono<Void> deleteEntry(UUID userId, UUID entryId);

    Flux<ExpenseEntry> listForDate(UUID userId, LocalDate date);

    Mono<DailyExpenseSummary> getDailySummary(UUID userId, LocalDate date);
}
