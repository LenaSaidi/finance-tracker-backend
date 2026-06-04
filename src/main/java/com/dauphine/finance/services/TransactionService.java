package com.dauphine.finance.services;

import com.dauphine.finance.exceptions.CategoryNotFoundException;
import com.dauphine.finance.exceptions.TransactionNotFoundException;
import com.dauphine.finance.models.Category;
import com.dauphine.finance.models.Transaction;
import com.dauphine.finance.models.TransactionType;
import com.dauphine.finance.repositories.CategoryRepository;
import com.dauphine.finance.repositories.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    public record TransactionSummary(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance) {
    }

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Transaction> getAll(String search,
                                    TransactionType type,
                                    UUID categoryId,
                                    LocalDateTime startDate,
                                    LocalDateTime endDate,
                                    BigDecimal minAmount,
                                    BigDecimal maxAmount) {
        String normalizedSearch = null;
        if (search != null && !search.isBlank()) {
            normalizedSearch = "%" + search.toLowerCase() + "%";
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException("minAmount must be less than or equal to maxAmount");
        }
        return transactionRepository.findByFilters(
                normalizedSearch,
                type,
                categoryId,
                startDate,
                endDate,
                minAmount,
                maxAmount
        );
    }

    public Transaction getById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public Transaction create(Transaction transaction, UUID categoryId) {
        validateTransaction(transaction);
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            transaction.setCategory(category);
        }
        return transactionRepository.save(transaction);
    }

    public Transaction update(UUID id, Transaction updated, UUID categoryId) {
        validateTransaction(updated);
        Transaction existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setAmount(updated.getAmount());
        existing.setType(updated.getType());
        existing.setDate(updated.getDate());
        existing.setDescription(updated.getDescription());

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            existing.setCategory(category);
        } else {
            existing.setCategory(null);
        }

        return transactionRepository.save(existing);
    }

    public void delete(UUID id) {
        Transaction existing = getById(id);
        transactionRepository.delete(existing);
    }

    public List<Transaction> getByCategoryId(UUID categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return transactionRepository.findByCategoryId(categoryId);
    }

    public TransactionSummary getSummary() {
        List<Transaction> transactions = transactionRepository.findAll();
        return buildSummary(transactions);
    }

    public TransactionSummary getMonthlySummary(int year, int month) {
        if (year <= 0) {
            throw new IllegalArgumentException("year must be positive");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month must be between 1 and 12");
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        List<Transaction> transactions = transactionRepository.findByDateBetween(start, end);
        return buildSummary(transactions);
    }

    public List<Map<String, Object>> getExpenseSummaryByCategory() {
        List<Object[]> rows = transactionRepository.findExpenseSummaryByCategory(TransactionType.EXPENSE);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("categoryId", row[0]);
            item.put("categoryName", row[1]);
            item.put("totalAmount", row[2]);
            result.add(item);
        }

        return result;
    }

    private TransactionSummary buildSummary(List<Transaction> transactions) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpense);
        return new TransactionSummary(totalIncome, totalExpense, balance);
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getTitle() == null || transaction.getTitle().isBlank()) {
            throw new IllegalArgumentException("Transaction title is required");
        }
        if (transaction.getAmount() == null) {
            throw new IllegalArgumentException("Transaction amount is required");
        }
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
        if (transaction.getDate() == null) {
            throw new IllegalArgumentException("Transaction date is required");
        }
    }
}
