package com.fintrack.transactions.dto;

import com.fintrack.transactions.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID userId,
        TransactionType type,
        String Category,
        BigDecimal amount,
        String description,
        Instant createdAt
) {
}
