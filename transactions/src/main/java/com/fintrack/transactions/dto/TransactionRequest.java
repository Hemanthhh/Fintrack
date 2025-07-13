package com.fintrack.transactions.dto;

import com.fintrack.transactions.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull TransactionType type,
        @NotBlank String category,
        @NotNull @DecimalMin(value = "0.0", inclusive = false)BigDecimal amount,
        String description
        ) {
}
