package com.example.tracker.api.protein;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import com.example.tracker.application.protein.ProteinService;
import com.example.tracker.security.UserPrincipal;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/protein")
public class ProteinController {

    private final ProteinService proteinService;

    public ProteinController(ProteinService proteinService) {
        this.proteinService = proteinService;
    }

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProteinDTO.ProteinEntryResponse> addEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProteinDTO.CreateProteinEntryRequest request) {
        return proteinService.addEntry(
                principal.userId(), request.foodName(), request.gramsConsumed(),
                request.proteinGrams(), request.entryDate())
                .map(ProteinDTO.ProteinEntryResponse::from);
    }

    @PutMapping("/entries/{entryId}")
    public Mono<ProteinDTO.ProteinEntryResponse> updateEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID entryId,
            @Valid @RequestBody ProteinDTO.UpdateProteinEntryRequest request) {
        return proteinService.updateEntry(principal.userId(), entryId, request.proteinGrams())
                .map(ProteinDTO.ProteinEntryResponse::from);
    }

    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID entryId) {
        return proteinService.deleteEntry(principal.userId(), entryId);
    }

    @GetMapping("/entries")
    public Flux<ProteinDTO.ProteinEntryResponse> listForDate(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam LocalDate date) {
        return proteinService.listForDate(principal.userId(), date)
                .map(ProteinDTO.ProteinEntryResponse::from);
    }

    @GetMapping("/summary")
    public Mono<ProteinDTO.DailySummaryResponse> getDailySummary(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam LocalDate date) {
        return proteinService.getDailySummary(principal.userId(), date)
                .map(ProteinDTO.DailySummaryResponse::from);
    }
}
