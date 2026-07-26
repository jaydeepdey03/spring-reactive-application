package com.example.tracker.domain.protein;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface ProteinEntryRepository extends ReactiveCrudRepository<ProteinEntry, UUID> {

    Flux<ProteinEntry> findByUserIdAndEntryDateOrderByCreatedAtDesc(UUID userId, LocalDate entryDate);

    Flux<ProteinEntry> findByUserIdAndEntryDateBetweenOrderByEntryDateDesc(
            UUID userId, LocalDate from, LocalDate to);
}
