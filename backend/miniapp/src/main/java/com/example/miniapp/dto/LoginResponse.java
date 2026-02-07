package com.example.miniapp.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
