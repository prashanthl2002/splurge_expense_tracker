package com.rpl.splurge.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rpl.splurge.model.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>{

	// Find the budget for a specific user + month + year
    // Optional means it might not exist yet — caller handles the null case
    Optional<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
}

