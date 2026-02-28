package com.rpl.splurge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Budget — stores how much the user wants to spend per month.
 * One row per user per month. e.g. userId=1, month=2, year=2026, amount=25000
 */
@Entity
@Table(
    name = "budgets",
    uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "month", "year"})
    // ^ prevents duplicate budget for same month
)

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @NotNull(message = "Month is required")
    @Column(nullable = false)
    private Integer month;   // 1–12

    @NotNull(message = "Year is required")
    @Column(nullable = false)
    private Integer year;    // e.g. 2026

    @NotNull(message = "Budget amount is required")
    @Min(value = 1, message = "Budget must be at least ₹1")
    @Column(nullable = false)
    private Double amount;   // e.g. 25000.0
}

