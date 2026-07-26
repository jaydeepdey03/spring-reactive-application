package com.example.tracker.domain.protein;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("protein_entries")
@Data
@Builder
public class ProteinEntry {
    @Id
    private UUID id;

    private UUID userId;
    private String foodName;
    private BigDecimal gramsConsumed;
    private BigDecimal proteinGrams;
    private LocalDate entryDate;
    private Instant createdAt;

    @Version
    private Long version;
}
