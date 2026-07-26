package com.example.tracker.domain.expense;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("expense_entries")
@Data
@Builder
public class ExpenseEntry {
    @Id
    private UUID id;

    private UUID userId;
    private String description;
    private String category;
    private BigDecimal amount;
    private String currency;
    private LocalDate entryDate;
    private Instant createdAt;

    @Version
    private Long version;
}
