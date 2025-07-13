package com.fintrack.transactions.service;

import com.fintrack.transactions.dto.TransactionRequest;
import com.fintrack.transactions.dto.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse create(UUID userId, TransactionRequest request);
    List<TransactionResponse> getAllTransactions(UUID userId);
    void delete(UUID userId, UUID transactionId);
}
