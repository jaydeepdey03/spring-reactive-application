package com.example.tracker.domain.protein;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("daily_protein_summary")
@Data
@Builder
public class DailyProteinSummary {
    @Id
    private UUID id;

    private UUID userId;
    private LocalDate summaryDate;
    private BigDecimal totalProteinGrams;
    private Integer entryCount;
    private Instant updatedAt;
}
