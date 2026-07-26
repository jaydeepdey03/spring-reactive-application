package com.example.tracker.api.protein;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.tracker.domain.protein.DailyProteinSummary;
import com.example.tracker.domain.protein.ProteinEntry;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProteinDTO {
    public record CreateProteinEntryRequest(
            @NotBlank String foodName,
            @NotNull @DecimalMin("0.0") BigDecimal gramsConsumed,
            @NotNull @DecimalMin("0.0") BigDecimal proteinGrams,
            @NotNull LocalDate entryDate) {
    }

    public record UpdateProteinEntryRequest(
            @NotNull @DecimalMin("0.0") BigDecimal proteinGrams) {
    }

    public record ProteinEntryResponse(
            UUID id, String foodName, BigDecimal gramsConsumed,
            BigDecimal proteinGrams, LocalDate entryDate) {
        public static ProteinEntryResponse from(ProteinEntry e) {
            return new ProteinEntryResponse(
                    e.getId(), e.getFoodName(), e.getGramsConsumed(), e.getProteinGrams(), e.getEntryDate());
        }
    }

    public record DailySummaryResponse(LocalDate date, BigDecimal totalProteinGrams, int entryCount) {
        public static DailySummaryResponse from(DailyProteinSummary s) {
            return new DailySummaryResponse(s.getSummaryDate(), s.getTotalProteinGrams(), s.getEntryCount());
        }
    }
}
