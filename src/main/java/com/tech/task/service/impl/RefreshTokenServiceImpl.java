package com.tech.task.service.impl;

import com.tech.task.exception.InvalidRefreshTokenException;
import com.tech.task.model.User;
import com.tech.task.model.token.RefreshToken;
import com.tech.task.repository.RefreshTokenRepository;
import com.tech.task.service.RefreshTokenService;
import com.tech.task.utils.TokenGenerator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenGenerator tokenGenerator;
    private final JwtServiceImpl jwtService;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, TokenGenerator tokenGenerator, JwtServiceImpl jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenGenerator = tokenGenerator;
        this.jwtService = jwtService;
    }

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
        return token;
    }

    public boolean isRefreshTokenValid(String token) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(token);
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();
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

    public void deleteRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public void deleteRefreshTokenById(Long id){
        refreshTokenRepository.deleteById(id);
    }
}
