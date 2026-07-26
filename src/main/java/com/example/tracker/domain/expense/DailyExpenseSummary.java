package com.example.tracker.domain.expense;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("daily_expense_summary")
@Data
@Builder
public class DailyExpenseSummary {
    @Id
    private UUID id;

    private UUID userId;
    private LocalDate summaryDate;
    private BigDecimal totalAmount;
    private Integer entryCount;
    private Instant updatedAt;
}
