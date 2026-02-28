package com.rpl.splurge.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rpl.splurge.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // All transactions for a user, newest first
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    // Filter by category
    List<Transaction> findByUserIdAndCategoryOrderByDateDesc(Long userId, String category);

    // Transactions for a specific month + year — native PostgreSQL query
    @Query(value = "SELECT * FROM transactions t " +
                   "WHERE t.user_id = :userId " +
                   "AND EXTRACT(MONTH FROM t.date) = :month " +
                   "AND EXTRACT(YEAR FROM t.date) = :year " +
                   "ORDER BY t.date DESC", 
           nativeQuery = true)
    List<Transaction> findByUserIdAndMonthAndYear(
        @Param("userId") Long userId,
        @Param("month") int month,
        @Param("year") int year
    );

    // Total spent for a month
    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t " +
                   "WHERE t.user_id = :userId " +
                   "AND EXTRACT(MONTH FROM t.date) = :month " +
                   "AND EXTRACT(YEAR FROM t.date) = :year",
           nativeQuery = true)
    Double totalSpentForMonth(
        @Param("userId") Long userId,
        @Param("month") int month,
        @Param("year") int year
    );

    // Spend broken down by category for a month
    @Query(value = "SELECT t.category, SUM(t.amount) FROM transactions t " +
                   "WHERE t.user_id = :userId " +
                   "AND EXTRACT(MONTH FROM t.date) = :month " +
                   "AND EXTRACT(YEAR FROM t.date) = :year " +
                   "GROUP BY t.category",
           nativeQuery = true)
    List<Object[]> findSpendByCategoryForMonth(
        @Param("userId") Long userId,
        @Param("month") int month,
        @Param("year") int year
    );

}

