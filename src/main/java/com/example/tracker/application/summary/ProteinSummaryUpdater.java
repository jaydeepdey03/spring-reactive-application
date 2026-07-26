package com.example.tracker.application.summary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class ProteinSummaryUpdater implements SummaryUpdater {
    private static final String UPSERT_SQL = """
            INSERT INTO daily_protein_summary (id, user_id, summary_date, total_protein_grams, entry_count, updated_at)
            VALUES (gen_random_uuid(), :userId, :date, :amount, :count, now())
            ON CONFLICT (user_id, summary_date)
            DO UPDATE SET
                total_protein_grams = daily_protein_summary.total_protein_grams + EXCLUDED.total_protein_grams,
                entry_count = daily_protein_summary.entry_count + EXCLUDED.entry_count,
                updated_at = now()
            """;

    private final DatabaseClient databaseClient;

    public ProteinSummaryUpdater(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Void> applyDelta(UUID userId, LocalDate date, BigDecimal amountDelta, int countDelta) {
        {
            return databaseClient.sql(UPSERT_SQL)
                    .bind("userId", userId)
                    .bind("date", date)
                    .bind("amount", amountDelta)
                    .bind("count", countDelta)
                    .fetch()
                    .rowsUpdated()
                    .then();
        }
    }
}
