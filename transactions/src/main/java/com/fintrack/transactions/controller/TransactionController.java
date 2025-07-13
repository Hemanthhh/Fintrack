package com.fintrack.transactions.controller;

import com.fintrack.transactions.dto.TransactionRequest;
import com.fintrack.transactions.dto.TransactionResponse;
import com.fintrack.transactions.service.TransactionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final HttpServletRequest request;

    @Value("${jwt.secret}")
    private String secretKey;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @Valid @RequestBody TransactionRequest request
            ){
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(transactionService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(transactionService.getAllTransactions(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ){
        UUID userId = getCurrentUserId();
        transactionService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header missing or invalid");
        }
        
        String jwtToken = authorizationHeader.substring(7);
        String username = extractUsernameFromToken(jwtToken);
        
        try {
            return UUID.fromString(username);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user ID format in JWT token");
        }
    }
    
    private String extractUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token");
        }
    }
    
    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
