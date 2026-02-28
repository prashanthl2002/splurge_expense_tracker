package com.rpl.splurge.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rpl.splurge.dto.TransactionDTOs.CreateTransactionRequest;
import com.rpl.splurge.dto.TransactionDTOs.SetBudgetRequest;
import com.rpl.splurge.dto.TransactionDTOs.SummaryResponse;
import com.rpl.splurge.dto.TransactionDTOs.TransactionResponse;
import com.rpl.splurge.model.Budget;
import com.rpl.splurge.model.Transaction;
import com.rpl.splurge.repo.BudgetRepository;
import com.rpl.splurge.repo.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
public class TransactionService {
	
	@Autowired
	private  TransactionRepository transactionRepo;
	
	@Autowired
    private  BudgetRepository budgetRepo;

    // Hardcoded until we add auth in v2
    private static final Long USER_ID = 1L;

    // ─── ADD TRANSACTION ───────────────────────────────────────────

    public TransactionResponse addTransaction(CreateTransactionRequest req) {

        // Map the incoming request → Transaction entity
        Transaction txn = new Transaction();
        txn.setUserId(USER_ID);
        txn.setName(req.getName());
        txn.setAmount(req.getAmount());
        txn.setCategory(req.getCategory().toLowerCase()); // normalize to lowercase
        txn.setNote(req.getNote());
        txn.setDate(req.getDate() != null ? req.getDate() : LocalDate.now());

        // Save to DB — Spring Data handles the INSERT
        Transaction saved = transactionRepo.save(txn);

        return toResponse(saved);
    }

    // ─── GET ALL TRANSACTIONS ──────────────────────────────────────

    public List<TransactionResponse> getAllTransactions(String category) {

        List<Transaction> transactions;

        if (category != null && !category.isBlank()) {
            // Filter by category if provided
            transactions = transactionRepo
                .findByUserIdAndCategoryOrderByDateDesc(USER_ID, category.toLowerCase());
        } else {
            // Otherwise return everything, newest first
            transactions = transactionRepo.findByUserIdOrderByDateDesc(USER_ID);
        }

        // Convert each Transaction entity → TransactionResponse DTO
        return transactions.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ─── GET SUMMARY ───────────────────────────────────────────────

    public SummaryResponse getSummary(int month, int year) {

        // 1. Total spent this month
        Double totalSpent = transactionRepo.totalSpentForMonth(USER_ID, month, year);

        // 2. Get budget for this month (might not be set yet)
        Double budgetAmount = budgetRepo
            .findByUserIdAndMonthAndYear(USER_ID, month, year)
            .map(Budget::getAmount)
            .orElse(0.0); // 0 if no budget set

        // 3. Spend broken down by category
        List<Object[]> rawCategoryData = transactionRepo
            .findSpendByCategoryForMonth(USER_ID, month, year);

        // Convert the raw Object[] list → a clean Map<String, Double>
        // Object[0] = category name, Object[1] = total amount
        Map<String, Double> byCategory = new HashMap<>();
        for (Object[] row : rawCategoryData) {
            String cat = (String) row[0];
            Double total = ((Number) row[1]).doubleValue();
            byCategory.put(cat, total);
        }

        // 4. Build and return the response
        SummaryResponse summary = new SummaryResponse();
        summary.setMonth(month);
        summary.setYear(year);
        summary.setTotalSpent(totalSpent);
        summary.setBudget(budgetAmount);
        summary.setRemaining(budgetAmount - totalSpent);
        summary.setPercentageUsed(
            budgetAmount > 0 ? (totalSpent / budgetAmount) * 100 : 0
        );
        summary.setByCategory(byCategory);

        return summary;
    }

    // ─── SET BUDGET ────────────────────────────────────────────────

    public Budget setBudget(SetBudgetRequest req) {

        // Check if a budget already exists for this month
        Budget budget = budgetRepo
            .findByUserIdAndMonthAndYear(USER_ID, req.getMonth(), req.getYear())
            .orElse(new Budget()); // create new if not found

        // Update fields — works for both create and update
        budget.setUserId(USER_ID);
        budget.setMonth(req.getMonth());
        budget.setYear(req.getYear());
        budget.setAmount(req.getAmount());

        return budgetRepo.save(budget); // INSERT or UPDATE
    }

    // ─── DELETE TRANSACTION ────────────────────────────────────────

    public void deleteTransaction(Long id) {
        // Check it exists first — throws exception if not found
        transactionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        transactionRepo.deleteById(id);
    }

    // ─── HELPER — Entity → Response DTO ───────────────────────────

    private TransactionResponse toResponse(Transaction txn) {
        TransactionResponse res = new TransactionResponse();
        res.setId(txn.getId());
        res.setName(txn.getName());
        res.setAmount(txn.getAmount());
        res.setCategory(txn.getCategory());
        res.setNote(txn.getNote());
        res.setDate(txn.getDate());
        return res;
    }
}
