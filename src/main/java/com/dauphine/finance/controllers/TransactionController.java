package com.dauphine.finance.controllers;

import com.dauphine.finance.dto.TransactionCreateDTO;
import com.dauphine.finance.dto.TransactionUpdateDTO;
import com.dauphine.finance.models.Transaction;
import com.dauphine.finance.models.TransactionType;
import com.dauphine.finance.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transactions", description = "Manage income and expense entries")
@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "List transactions", description = "Filter by type, category, date range, amount, or search term.")
    public ResponseEntity<List<Transaction>> getAll(
            @RequestParam(required = false, name = "q") String search,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false, name = "category") String category,
            @RequestParam(required = false, name = "categoryId") String categoryId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount) {
        // Accept both category and categoryId params for backward compatibility.
        UUID resolvedCategory = null;
        try {
            if (categoryId != null && !categoryId.isBlank()) {
                resolvedCategory = UUID.fromString(categoryId);
            } else if (category != null && !category.isBlank()) {
                resolvedCategory = UUID.fromString(category);
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("category must be a valid UUID");
        }
        List<Transaction> transactions = transactionService.getAll(
            search,
            type,
            resolvedCategory,
            startDate,
            endDate,
            minAmount,
            maxAmount
        );
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by id")
    public ResponseEntity<Transaction> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create transaction")
    public ResponseEntity<Transaction> create(@RequestBody TransactionCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Transaction payload is required");
        }
        Transaction transaction = new Transaction();
        transaction.setTitle(dto.getTitle());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        transaction.setDescription(dto.getDescription());

        Transaction created = transactionService.create(transaction, dto.getCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transaction")
    public ResponseEntity<Transaction> update(@PathVariable UUID id, @RequestBody TransactionUpdateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Transaction payload is required");
        }
        Transaction transaction = new Transaction();
        transaction.setTitle(dto.getTitle());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        transaction.setDescription(dto.getDescription());

        Transaction updated = transactionService.update(id, transaction, dto.getCategoryId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    @Operation(summary = "Get transaction summary")
    public ResponseEntity<TransactionService.TransactionSummary> getSummary() {
        return ResponseEntity.ok(transactionService.getSummary());
    }

    @GetMapping("/monthly-summary")
    @Operation(summary = "Get monthly summary")
    public ResponseEntity<TransactionService.TransactionSummary> getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(transactionService.getMonthlySummary(year, month));
    }

    @GetMapping("/expenses-by-category")
    @Operation(summary = "Get expenses by category")
    public ResponseEntity<List<Map<String, Object>>> getExpensesByCategory() {
        return ResponseEntity.ok(transactionService.getExpenseSummaryByCategory());
    }
}
