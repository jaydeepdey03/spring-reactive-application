package com.example.tracker.application.expense;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.OptimisticLockingFailureException;

import com.example.tracker.application.summary.SummaryUpdater;
import com.example.tracker.domain.expense.DailyExpenseSummary;
import com.example.tracker.domain.expense.DailyExpenseSummaryRepository;
import com.example.tracker.domain.expense.ExpenseEntry;
import com.example.tracker.domain.expense.ExpenseEntryRepository;
import com.example.tracker.exceptions.ForbiddenException;
import com.example.tracker.exceptions.ResourceNotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseEntryRepository expenseEntryRepository;
    private final DailyExpenseSummaryRepository summaryRepository;
    private final SummaryUpdater summaryUpdater;

    public ExpenseServiceImpl(ExpenseEntryRepository expenseEntryRepository,
            DailyExpenseSummaryRepository summaryRepository,
            @Qualifier("expenseSummaryUpdater") SummaryUpdater summaryUpdater) {
        this.expenseEntryRepository = expenseEntryRepository;
        this.summaryRepository = summaryRepository;
        this.summaryUpdater = summaryUpdater;
    }

    @Override
    public Mono<ExpenseEntry> addEntry(UUID userId, String description, String category, BigDecimal amount,
            String currency,
            LocalDate date) {
        ExpenseEntry entry = ExpenseEntry.builder()
                .userId(userId)
                .description(description)
                .category(category)
                .amount(amount)
                .currency(currency)
                .entryDate(date)
                .createdAt(Instant.now())
                .build();

        return expenseEntryRepository.save(entry)
                .flatMap(saved -> summaryUpdater.applyDelta(userId, date, amount, 1).thenReturn(saved));
    }

    @Override
    public Mono<ExpenseEntry> updateEntry(UUID userId, UUID entryId, BigDecimal newAmount) {
        return expenseEntryRepository.findById(entryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Expense Not Found"))).flatMap(expense -> {
                    if (!expense.getUserId().equals(userId)) {
                        return Mono.error(new ForbiddenException("Not your entry"));
                    }

                    BigDecimal delta = newAmount.subtract(expense.getAmount());

                    ExpenseEntry updated = ExpenseEntry.builder()
                            .id(entryId)
                            .userId(userId)
                            .description(expense.getDescription())
                            .category(expense.getCategory())
                            .amount(newAmount)
                            .currency(expense.getCurrency())
                            .currency(expense.getCurrency())
                            .entryDate(expense.getEntryDate())
                            .createdAt(expense.getCreatedAt())
                            .version(expense.getVersion())
                            .build();
                    return expenseEntryRepository.save(updated).flatMap(saved -> summaryUpdater
                            .applyDelta(userId, expense.getEntryDate(), delta, 0).thenReturn(saved));
                }).retryWhen(Retry.backoff(3, Duration.ofMillis(50))
                        .filter(OptimisticLockingFailureException.class::isInstance));
    }

    @Override
    public Mono<Void> deleteEntry(UUID userId, UUID entryId) {
        return expenseEntryRepository.findById(entryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Expense entry not found")))
                .flatMap(entry -> {
                    if (!entry.getUserId().equals(userId)) {
                        return Mono.error(new ForbiddenException("Not your entry"));
                    }
                    return expenseEntryRepository.delete(entry)
                            .then(summaryUpdater.applyDelta(
                                    userId, entry.getEntryDate(), entry.getAmount().negate(), -1));
                });
    }

    @Override
    public Flux<ExpenseEntry> listForDate(UUID userId, LocalDate date) {
        return expenseEntryRepository.findByUserIdAndEntryDateOrderByCreatedAtDesc(userId, date);
    }

    @Override
    public Mono<DailyExpenseSummary> getDailySummary(UUID userId, LocalDate date) {
        return summaryRepository.findByUserIdAndSummaryDate(userId, date)
                .defaultIfEmpty(DailyExpenseSummary.builder()
                        .userId(userId)
                        .summaryDate(date)
                        .totalAmount(BigDecimal.ZERO)
                        .entryCount(0)
                        .build());
    }
}
