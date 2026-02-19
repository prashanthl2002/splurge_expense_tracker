package com.rpl.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment ID
    private Long id;

    // Hardcoded for now — will become a FK to users table in v2
    private Long userId;

    @NotBlank(message = "Name cannot be empty")
    @Column(nullable = false)
    private String name;           // e.g. "Zomato dinner"

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least ₹1")
    @Column(nullable = false)
    private Double amount;         // e.g. 680.0

    @NotBlank(message = "Category cannot be empty")
    @Column(nullable = false)
    private String category;       // e.g. "food", "travel", "fun"

    private String note;           // optional note, e.g. "team lunch"

    @Column(nullable = false)
    private LocalDate date;        // e.g. 2026-02-18

    // Runs before every INSERT — if date not set, default to today
    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }
}