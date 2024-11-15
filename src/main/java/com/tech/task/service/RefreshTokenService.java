package com.tech.task.service;

import com.tech.task.exception.InvalidRefreshTokenException;
import com.tech.task.model.User;
import com.tech.task.model.token.RefreshToken;
import com.tech.task.repository.RefreshTokenRepository;
import com.tech.task.utils.TokenGenerator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final TokenGenerator tokenGenerator;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService, TokenGenerator tokenGenerator) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.tokenGenerator = tokenGenerator;
    }

    @Transactional
    public String generateRefreshToken(UserDetails userDetails) {
        User user = (User) userDetails;
        if (refreshTokenRepository.existsByUser(user)) {
            System.out.println(refreshTokenRepository.existsByUser(user));
            deleteRefreshToken(user);
        }
        System.out.println(refreshTokenRepository.existsByUser(user));
        String token = tokenGenerator.generateToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusMillis(60 * 60 * 24 * 7 * 1000));
        refreshTokenRepository.save(refreshToken);
        System.out.println("Generated refresh token: " + token);
        return token;
    }

    public boolean isRefreshTokenValid(String token) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(token);
        System.out.println("Refresh token found: " + refreshTokenOptional.isPresent());
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();
            System.out.println("Refresh token expiry date: " + refreshToken.getExpiryDate());
            return !refreshToken.getExpiryDate().isBefore(Instant.now());
        }
        return false;
    }

    public UserDetails getUserDetailsFromRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
    }

    public String generateTokenFromRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(t -> jwtService.generateToken(t.getUser()))
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
    }

    @Transactional
    public void deleteRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void deleteRefreshTokenById(Long id){
        refreshTokenRepository.deleteById(id);
    }
}
