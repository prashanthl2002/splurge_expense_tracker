package com.rpl.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rpl.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	// Fetch all transactions for a user, newest first
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    // Fetch filtered by category
    List<Transaction> findByUserIdAndCategoryOrderByDateDesc(Long userId, String category);

    // Fetch all for a specific month + year
    List<Transaction> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    // Custom JPQL query — total spent per category for a given month
    // Returns a list of Object[] where [0]=category, [1]=total
    @Query("""
        SELECT t.category, SUM(t.amount)
        FROM Transaction t
        WHERE t.userId = :userId
          AND MONTH(t.date) = :month
          AND YEAR(t.date) = :year
        GROUP BY t.category
    """)
    List<Object[]> findSpendByCategoryForMonth(
        @Param("userId") Long userId,
        @Param("month") int month,
        @Param("year") int year
    );

    // Total spent this month
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.userId = :userId
          AND MONTH(t.date) = :month
          AND YEAR(t.date) = :year
    """)
    Double totalSpentForMonth(
        @Param("userId") Long userId,
        @Param("month") int month,
        @Param("year") int year
    );

    // Helper — Spring derives this from method name automatically
    // "find transactions by userId where date's month = month and date's year = year"
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND MONTH(t.date) = :month AND YEAR(t.date) = :year ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndMonthAndYear(
        @Param("userId") Long userId,
        @Param("month") int month,
        @Param("year") int year
    );

}
