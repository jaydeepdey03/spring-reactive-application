package com.example.tracker.application.summary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component("expenseSummaryUpdater")
public class ExpenseSummaryUpdater implements SummaryUpdater {
    private static final String UPSERT_SQL = """
            INSERT INTO daily_expense_summary (id, user_id, summary_date, total_amount, entry_count, updated_at)
            VALUES (gen_random_uuid(), :userId, :date, :amount, :count, now())
            ON CONFLICT (user_id, summary_date)
            DO UPDATE SET
                total_amount = daily_expense_summary.total_amount + EXCLUDED.total_amount,
                entry_count = daily_expense_summary.entry_count + EXCLUDED.entry_count,
                updated_at = now()
            """;

    private final DatabaseClient databaseClient;

    public ExpenseSummaryUpdater(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Void> applyDelta(UUID userId, LocalDate date, BigDecimal amountDelta, int countDelta) {
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
