package com.rpl.dto;

import java.time.LocalDate;
import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class TransactionDTOs {

	// ─── REQUEST ───────────────────────────────────────────────────
	// What the frontend sends when adding a transaction

	@Data
	public static class CreateTransactionRequest {

		@NotBlank(message = "Name is required")
		private String name; // "Zomato dinner"

		@NotNull(message = "Amount is required")
		@Min(value = 1, message = "Amount must be at least 1")
		private Double amount; // 680.0

		@NotBlank(message = "Category is required")
		private String category; // "food"

		private String note; // optional

		private LocalDate date; // optional, defaults to today
	}

	// ─── RESPONSE — single transaction ─────────────────────────────

	@Data
	public static class TransactionResponse {
		private Long id;
		private String name;
		private Double amount;
		private String category;
		private String note;
		private LocalDate date;
	}

	// ─── RESPONSE — summary (used in /summary endpoint) ────────────

	@Data
	public static class SummaryResponse {
		private int month;
		private int year;
		private Double totalSpent;
		private Double budget;
		private Double remaining;
		private Double percentageUsed;
		private Map<String, Double> byCategory;

	}

	// ─── BUDGET ────────────────────────────────────────────────────

	@Data
	public static class SetBudgetRequest {

		@NotNull(message = "Month is required")
		@Min(value = 1)
		private Integer month;

		@NotNull(message = "Year is required")
		private Integer year;

		@NotNull(message = "Amount is required")
		@Min(value = 1, message = "Budget must be at least ₹1")
		private Double amount;
	}

}
