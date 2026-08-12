package com.example.tracker.application.protein;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.example.tracker.application.summary.SummaryUpdater;
import com.example.tracker.domain.protein.DailyProteinSummary;
import com.example.tracker.domain.protein.DailyProteinSummaryRepository;
import com.example.tracker.domain.protein.ProteinEntry;
import com.example.tracker.domain.protein.ProteinEntryRepository;
import com.example.tracker.exceptions.ForbiddenException;
import com.example.tracker.exceptions.ResourceNotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class ProteinServiceImpl implements ProteinService {
    private final ProteinEntryRepository expenseEntryRepository;
    private final DailyProteinSummaryRepository summaryRepository;
    private final SummaryUpdater summaryUpdater;

    public ProteinServiceImpl(
            ProteinEntryRepository expenseEntryRepository,
            DailyProteinSummaryRepository summaryRepository,
            @Qualifier("proteinSummaryUpdater") SummaryUpdater summaryUpdater) {
        this.expenseEntryRepository = expenseEntryRepository;
        this.summaryRepository = summaryRepository;
        this.summaryUpdater = summaryUpdater;
    }

    @Override
    public Mono<ProteinEntry> addEntry(UUID userId, String foodName, BigDecimal gramsConsumed,
            BigDecimal proteinGrams, LocalDate date) {
        ProteinEntry entry = ProteinEntry.builder()
                .userId(userId)
                .foodName(foodName)
                .gramsConsumed(gramsConsumed)
                .proteinGrams(proteinGrams)
                .entryDate(date)
                .createdAt(Instant.now())
                .build();

        return expenseEntryRepository.save(entry)
                // Chain the atomic summary upsert after the entry write. If this
                // step fails, the entry write is NOT automatically rolled back
                // (R2DBC has no ambient transaction here) — see note below.
                .flatMap(saved -> summaryUpdater.applyDelta(userId, date, proteinGrams, 1)
                        .thenReturn(saved));
        // PRODUCTION NOTE: wrap addEntry in a @Transactional reactive method
        // (TransactionalOperator) if the entry-insert + summary-upsert pair
        // must be atomic as a unit. Kept out here to keep the example focused
        // on the concurrency mechanics; see README section "Transactions".
    }

    @Override
    public Mono<ProteinEntry> updateEntry(UUID userId, UUID entryId, BigDecimal newProteinGrams) {
        return expenseEntryRepository.findById(entryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Protein entry not found")))
                .flatMap(entry -> {
                    if (!entry.getUserId().equals(userId)) {
                        return Mono.error(new ForbiddenException("Not your entry"));
                    }
                    BigDecimal delta = newProteinGrams.subtract(entry.getProteinGrams());
                    ProteinEntry updated = ProteinEntry.builder()
                            .id(entry.getId())
                            .userId(entry.getUserId())
                            .foodName(entry.getFoodName())
                            .gramsConsumed(entry.getGramsConsumed())
                            .proteinGrams(newProteinGrams)
                            .entryDate(entry.getEntryDate())
                            .createdAt(entry.getCreatedAt())
                            .version(entry.getVersion()) // must carry version for optimistic-lock check
                            .build();

                    return expenseEntryRepository.save(updated)
                            .flatMap(saved -> summaryUpdater
                                    .applyDelta(userId, entry.getEntryDate(), delta, 0)
                                    .thenReturn(saved));
                })
                // Two concurrent PUTs on the same entry: the second save() sees a
                // stale version and Spring Data throws OptimisticLockingFailureException.
                // Retry a few times with backoff — the retried attempt re-reads the
                // entry from findById() at the top, so it recomputes delta against
                // the now-current row instead of clobbering the other writer.
                .retryWhen(Retry.backoff(3, Duration.ofMillis(50))
                        .filter(OptimisticLockingFailureException.class::isInstance));
    }

    @Override
    public Mono<Void> deleteEntry(UUID userId, UUID entryId) {
        return expenseEntryRepository.findById(entryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Protein entry not found")))
                .flatMap(entry -> {
                    if (!entry.getUserId().equals(userId)) {
                        return Mono.error(new ForbiddenException("Not your entry"));
                    }
                    return expenseEntryRepository.delete(entry)
                            .then(summaryUpdater.applyDelta(
                                    userId, entry.getEntryDate(), entry.getProteinGrams().negate(), -1));
                });
    }

    @Override
    public Flux<ProteinEntry> listForDate(UUID userId, LocalDate date) {
        return expenseEntryRepository.findByUserIdAndEntryDateOrderByCreatedAtDesc(userId, date);
    }

    @Override
    public Mono<DailyProteinSummary> getDailySummary(UUID userId, LocalDate date) {
        return summaryRepository.findByUserIdAndSummaryDate(userId, date)
                .defaultIfEmpty(DailyProteinSummary.builder()
                        .userId(userId)
                        .summaryDate(date)
                        .totalProteinGrams(BigDecimal.ZERO)
                        .entryCount(0)
                        .build());
    }
}
