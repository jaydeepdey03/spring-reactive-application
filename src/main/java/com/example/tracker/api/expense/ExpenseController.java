package com.example.tracker.api.expense;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracker.application.expense.ExpenseService;
import com.example.tracker.security.UserPrincipal;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private static final String DEFAULT_CURRENCY = "INR";

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ExpenseDTO.ExpenseEntryResponse> addEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ExpenseDTO.CreateExpenseEntryRequest request) {
        String currency = StringUtils.hasText(request.currency()) ? request.currency() : DEFAULT_CURRENCY;
        return expenseService.addEntry(
                principal.userId(), request.description(), request.category(),
                request.amount(), currency, request.entryDate())
                .map(ExpenseDTO.ExpenseEntryResponse::from);
    }

    @PutMapping("/entries/{entryId}")
    public Mono<ExpenseDTO.ExpenseEntryResponse> updateEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID entryId,
            @Valid @RequestBody ExpenseDTO.UpdateExpenseEntryRequest request) {
        return expenseService.updateEntry(principal.userId(), entryId, request.amount())
                .map(ExpenseDTO.ExpenseEntryResponse::from);
    }

    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID entryId) {
        return expenseService.deleteEntry(principal.userId(), entryId);
    }

    @GetMapping("/entries")
    public Flux<ExpenseDTO.ExpenseEntryResponse> listForDate(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam LocalDate date) {
        return expenseService.listForDate(principal.userId(), date)
                .map(ExpenseDTO.ExpenseEntryResponse::from);
    }

    @GetMapping("/summary")
    public Mono<ExpenseDTO.DailySummaryResponse> getDailySummary(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam LocalDate date) {
        return expenseService.getDailySummary(principal.userId(), date)
                .map(ExpenseDTO.DailySummaryResponse::from);
    }
}
