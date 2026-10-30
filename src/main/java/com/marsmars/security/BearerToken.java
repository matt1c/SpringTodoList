package com.marsmars.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

public final class BearerToken {
    private static final String PREFIX = "Bearer ";

    private BearerToken() {
    }

    public static Optional<String> resolve(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith(PREFIX))
                .map(header -> header.substring(PREFIX.length()).trim())
                .filter(token -> !token.isEmpty());
    }
}
