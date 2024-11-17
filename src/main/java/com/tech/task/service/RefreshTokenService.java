package com.tech.task.service;

import com.tech.task.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface RefreshTokenService {

    String generateRefreshToken(UserDetails userDetails);

    boolean isRefreshTokenValid(String token);

    UserDetails getUserDetailsFromRefreshToken(String refreshToken);

    String generateTokenFromRefreshToken(String refreshToken);

    void deleteRefreshToken(User user);
}
