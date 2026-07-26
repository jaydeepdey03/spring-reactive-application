package com.example.tracker.api.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.tracker.domain.expense.DailyExpenseSummary;
import com.example.tracker.domain.expense.ExpenseEntry;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExpenseDTO {
    public record CreateExpenseEntryRequest(
            @NotBlank String description,
            @NotBlank String category,
            @NotNull @DecimalMin("0.0") BigDecimal amount,
            String currency, // optional, defaults to INR in service/entity default
            @NotNull LocalDate entryDate) {
    }

    public record UpdateExpenseEntryRequest(
            @NotNull @DecimalMin("0.0") BigDecimal amount) {
    }

    public record ExpenseEntryResponse(
            UUID id, String description, String category, BigDecimal amount,
            String currency, LocalDate entryDate) {
        public static ExpenseEntryResponse from(ExpenseEntry e) {
            return new ExpenseEntryResponse(
                    e.getId(), e.getDescription(), e.getCategory(), e.getAmount(),
                    e.getCurrency(), e.getEntryDate());
        }
    }

    public record DailySummaryResponse(LocalDate date, BigDecimal totalAmount, int entryCount) {
        public static DailySummaryResponse from(DailyExpenseSummary s) {
            return new DailySummaryResponse(s.getSummaryDate(), s.getTotalAmount(), s.getEntryCount());
        }
    }
}