package com.dauphine.finance.repositories;

import com.dauphine.finance.models.Transaction;
import com.dauphine.finance.models.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.type = :type")
    List<Transaction> findByType(@Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.category.id = :categoryId")
    List<Transaction> findByCategoryId(@Param("categoryId") UUID categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByDateBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE t.amount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findByAmountBetween(@Param("minAmount") BigDecimal minAmount,
                                          @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT t FROM Transaction t ORDER BY t.date DESC")
    List<Transaction> findAllByOrderByDateDesc();

    @Query("SELECT t.category.id, t.category.name, SUM(t.amount) " +
            "FROM Transaction t " +
            "WHERE t.type = :type AND t.category IS NOT NULL " +
            "GROUP BY t.category.id, t.category.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<Object[]> findExpenseSummaryByCategory(@Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t " +
            "WHERE (:search IS NULL OR LOWER(t.title) LIKE :search OR LOWER(COALESCE(t.description, '')) LIKE :search) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
            "AND (:startDate IS NULL OR t.date >= :startDate) " +
            "AND (:endDate IS NULL OR t.date <= :endDate) " +
            "AND (:minAmount IS NULL OR t.amount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR t.amount <= :maxAmount) " +
            "ORDER BY t.date DESC")
    List<Transaction> findByFilters(
            @Param("search") String search,
            @Param("type") TransactionType type,
            @Param("categoryId") UUID categoryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );
}

