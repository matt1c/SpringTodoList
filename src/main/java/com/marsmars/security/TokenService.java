package com.marsmars.security;

import com.marsmars.models.AuthToken;
import com.marsmars.models.User;
import com.marsmars.repositories.AuthTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class TokenService {
    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();
    private final AuthTokenRepository authTokenRepository;
    private final Duration ttl;

    public TokenService(AuthTokenRepository authTokenRepository,
                        @Value("${auth.token.ttl:PT24H}") Duration ttl) {
        this.authTokenRepository = authTokenRepository;
        this.ttl = ttl;
    }

    @Transactional
    public String issue(User user) {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        authTokenRepository.save(new AuthToken(null, hash(token), LocalDateTime.now().plus(ttl), user));
        return token;
    }

    @Transactional(readOnly = true)
    public Optional<User> findUser(String token) {
        return authTokenRepository.findByTokenHash(hash(token))
                .filter(authToken -> authToken.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(AuthToken::getUser);
    }

    @Transactional
    public void revoke(String token) {
        authTokenRepository.deleteByTokenHash(hash(token));
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
