package com.rpl.splurge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rpl.splurge.dto.TransactionDTOs.CreateTransactionRequest;
import com.rpl.splurge.dto.TransactionDTOs.SetBudgetRequest;
import com.rpl.splurge.dto.TransactionDTOs.SummaryResponse;
import com.rpl.splurge.dto.TransactionDTOs.TransactionResponse;
import com.rpl.splurge.model.Budget;
import com.rpl.splurge.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@PostMapping("/transactions")
	public ResponseEntity<TransactionResponse> addTransaction(@Valid @RequestBody CreateTransactionRequest request) {
		TransactionResponse response = transactionService.addTransaction(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
	}
	
	
	@GetMapping("/gettransactions")
	public ResponseEntity<List<TransactionResponse>> getTransactions(@RequestParam(required = false) String category) {
		List<TransactionResponse> transactions = transactionService.getAllTransactions(category);
		return ResponseEntity.ok(transactions); // 200 OK
	}
	
	@GetMapping("/transactions/summary")
	public ResponseEntity<SummaryResponse> getSummary(
			@RequestParam(defaultValue = "#{T(java.time.LocalDate).now().monthValue}") int month,
			@RequestParam(defaultValue = "#{T(java.time.LocalDate).now().year}") int year) {
		SummaryResponse summary = transactionService.getSummary(month, year);
		return ResponseEntity.ok(summary);
	}
	
	@DeleteMapping("/deletetransactions/{id}")
	public ResponseEntity<String> deleteTransaction(@PathVariable Long id) // grabs {id} from the URL path
	 {
		transactionService.deleteTransaction(id);
		return ResponseEntity.ok("Transaction deleted successfully");
	}
	
	@PutMapping("/budget")
	public ResponseEntity<Budget> setBudget(@Valid @RequestBody SetBudgetRequest request) {
		Budget budget = transactionService.setBudget(request);
		return ResponseEntity.ok(budget);
	}
	
	@GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Splurge API is running ✦");
    }

}
