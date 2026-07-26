package com.example.tracker.application.summary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import reactor.core.publisher.Mono;

public interface SummaryUpdater {
    Mono<Void> applyDelta(UUID userId, LocalDate date, BigDecimal amountDelta, int countDelta);

}
