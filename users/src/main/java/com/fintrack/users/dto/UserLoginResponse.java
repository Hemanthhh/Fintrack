package com.fintrack.users.dto;


public record UserLoginResponse(
        String token,
        String userId,
        String username,
        String email) {
}