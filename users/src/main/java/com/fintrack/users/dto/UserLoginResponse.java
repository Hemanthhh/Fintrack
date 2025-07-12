package com.fintrack.users.dto;

import lombok.Data;

public record UserLoginResponse(
        String token,
        String userId,
        String username,
        String email) {
}